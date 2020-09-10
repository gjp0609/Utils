package com.onysakura.localtools.db.sqlite

import com.onysakura.localtools.basic.StringUtils
import com.onysakura.localtools.basic.StringUtils.Companion.humpToUnderline
import com.onysakura.localtools.db.sqlite.SQLite.Companion.executeQuery
import com.onysakura.localtools.db.sqlite.SQLite.Companion.executeUpdate
import com.onysakura.localtools.log.CustomLogger
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.sql.ResultSet
import java.sql.SQLException

@Suppress("unused", "MemberVisibilityCanBePrivate")
class BaseRepository<T>(modelClass: Class<T>?) {

    private val log: CustomLogger.Log = CustomLogger.getLogger(BaseRepository::class)
    private val tableName: String
    private val fieldNames: MutableList<String> = mutableListOf()
    private var modelClass: Class<T>

    init {
        var modelClassNotNull: Class<T>? = modelClass
        if (modelClassNotNull == null) {
            modelClassNotNull = getModelClass()
        }
        require(modelClassNotNull != null) { "找不到实体对应的类" }
        this.modelClass = modelClassNotNull
        val fields: Array<Field> = modelClassNotNull.declaredFields
        for (i: Int in fields.indices) {
            fieldNames[i] = humpToUnderline(fields[i].name)
        }
        val tableName: Table = modelClassNotNull.getAnnotation(Table::class.java)
        this.tableName = tableName.name
        createTable()
    }

    @Suppress("UNCHECKED_CAST")
    fun getModelClass(): Class<T>? {
        val type: Type = javaClass.genericSuperclass
        return if (type is ParameterizedType) {
            val pType: Array<Type> = type.actualTypeArguments
            pType[0] as Class<T>
        } else {
            null
        }
    }

    fun createTable(): Int {
        var sql = "CREATE TABLE IF NOT EXISTS \$TABLE_NAME (\$CONTENT);"
        val content: MutableList<String> = mutableListOf()
        for (fieldName: String in fieldNames) {
            if ("ID".equals(fieldName, ignoreCase = true)) {
                content.add("ID TEXT PRIMARY KEY NOT NULL")
            } else {
                content.add("$fieldName TEXT")
            }
        }
        sql = sql.replace("\$TABLE_NAME", tableName)
                .replace("\$CONTENT", content.joinToString(", "))
        log.debug("create table sql: $sql")
        val i: Int = executeUpdate(sql)
        if (i >= 0) {
            log.debug("create table $tableName successfully")
        } else {
            log.warn("create table fail")
        }
        return i
    }

    fun selectAll(): MutableList<T> {
        var sql = "SELECT * FROM \$TABLE_NAME;"
        sql = sql.replace("\$TABLE_NAME", tableName)
        log.debug("select sql: $sql")
        val resultSet: ResultSet? = executeQuery(sql)
        return getResultList(resultSet, modelClass)
    }

    fun selectAll(sort: MutableMap<String, Sort>?): MutableList<T> {
        if (sort == null || sort.isEmpty()) {
            return selectAll()
        }
        var sql = "SELECT * FROM \$TABLE_NAME ORDER BY \$ORDER;"
        val order: MutableList<String> = mutableListOf()
        for ((key: String, value: Sort) in sort) {
            order.add(humpToUnderline(key) + " " + value.toString())
        }
        sql = sql.replace("\$TABLE_NAME", tableName)
                .replace("\$ORDER", order.joinToString(", "))
        log.debug("select sql: $sql")
        val resultSet: ResultSet? = executeQuery(sql)
        return getResultList(resultSet, modelClass)
    }

    fun select(model: T): MutableList<T> {
        val queries: MutableList<String> = mutableListOf()
        if (!getQueryParams(model, queries)) {
            return selectAll()
        }
        var sql = "SELECT * FROM \$TABLE_NAME WHERE \$QUERIES;"
        sql = sql.replace("\$TABLE_NAME", tableName)
                .replace("\$QUERIES", queries.joinToString(", "))
        log.debug("select sql: $sql")
        val resultSet: ResultSet? = executeQuery(sql)
        return getResultList(resultSet, modelClass)
    }

    fun select(model: T, sort: MutableMap<String, Sort>?): List<T> {
        val queries: MutableList<String> = mutableListOf()
        if (sort == null || sort.isEmpty()) {
            return select(model)
        }
        if (!getQueryParams(model, queries)) {
            return selectAll(sort)
        }
        var sql = "SELECT * FROM $tableName WHERE \$QUERIES ORDER BY \$ORDER;"
        val order = ArrayList<String>()
        for ((key: String, value: Sort) in sort) {
            order.add(humpToUnderline(key) + " " + value.toString())
        }
        sql = sql.replace("\$QUERIES", queries.joinToString(", "))
                .replace("\$ORDER", order.joinToString(", "))
        log.debug("select sql: $sql")
        val resultSet: ResultSet? = executeQuery(sql)
        return getResultList(resultSet, modelClass)
    }

    fun getQueryParams(model: T, queries: MutableList<String>): Boolean {
        var hasQueries = false
        for (fieldName: String in fieldNames) {
            try {
                val method: Method = modelClass.getDeclaredMethod(generateGetMethodName(fieldName))
                val invoke: Any? = method.invoke(model)
                if (invoke != null) {
                    hasQueries = true
                    queries.add(fieldName + " = '" + SQLite.escape(invoke.toString()) + "'")
                }
            } catch (e: ReflectiveOperationException) {
                log.warn("ReflectiveOperationException", e)
            }
        }
        return hasQueries
    }

    fun insert(t: T): T? {
        log.debug("update ${getInfo(t)}")
        var sql = "INSERT INTO $tableName (\$FIELDS) VALUES (\$VALUES);"
        val fields: MutableList<String> = mutableListOf()
        val values: MutableList<String> = mutableListOf()
        try {
            val setId: Method = modelClass.getDeclaredMethod("setId", String::class.java)
            setId.invoke(t, StringUtils.getNextId())
            for (fieldName: String in fieldNames) {
                fields.add(fieldName)
                val methodName: String = generateGetMethodName(fieldName)
                val method: Method = modelClass.getMethod(methodName)
                val invoke: Any? = method.invoke(t)
                if (invoke == null) {
                    values.add("NULL")
                } else {
                    values.add("'${SQLite.escape(invoke.toString())}'")
                }
            }
        } catch (e: Exception) {
            log.warn("insert fail", e)
        }
        sql = sql.replace("\$FIELDS", java.lang.String.join(", ", fields))
                .replace("\$VALUES", java.lang.String.join(", ", values))
        log.debug("insert sql: $sql")
        val update: Int = executeUpdate(sql)
        return if (update >= 0) t else null

    }

    fun update(t: T): T? {
        log.debug("update ${getInfo(t)}")
        var sql = "UPDATE $tableName SET \$VALUES WHERE ID = \$ID;"
        val values: MutableList<String> = mutableListOf()
        var id: String? = null
        try {
            val setId: Method = modelClass.getDeclaredMethod("getId")
            val idObject: Any? = setId.invoke(t)
            if (idObject != null) {
                id = idObject.toString()
                for (fieldName: String in fieldNames) {
                    val methodName: String = generateGetMethodName(fieldName)
                    val method: Method = modelClass.getMethod(methodName)
                    val invoke: Any? = method.invoke(t)
                    var value: String
                    if (invoke != null && !"ID".equals(fieldName, ignoreCase = true)) {
                        value = invoke.toString()
                        values.add(fieldName + " = '" + SQLite.escape(value) + "'")
                    }
                }
            }
        } catch (e: Exception) {
            log.warn("execute sql fail", e)
        }
        return if (id != null) {
            sql = sql.replace("\$ID", id)
                    .replace("\$VALUES", java.lang.String.join(", ", values))
            log.debug("update sql: $sql")
            val update = executeUpdate(sql)
            if (update >= 0) t else null
        } else {
            log.warn("update fail, no id value")
            null
        }
    }

    fun getInfo(t: T): String {
        var info: String? = null
        try {
            val toString: Method = t!!::class.java.getMethod("toString")
            val invoke: Any? = toString.invoke(t)
            if (invoke != null) {
                info = invoke.toString()
            }
        } catch (ignored: Exception) {
        }
        return info ?: t!!::class.java.name
    }

    fun delete(id: String): Int {
        val sql = "DELETE FROM $tableName WHERE ID = $id;"
        log.debug("delete sql: $sql")
        try {
            return executeUpdate(sql)
        } catch (e: Exception) {
            log.warn("execute sql fail", e)
        }
        return 0
    }

    private fun getResultList(resultSet: ResultSet?, modelClass: Class<T>): MutableList<T> {
        val list: MutableList<T> = ArrayList()
        if (resultSet != null) {
            try {
                while (resultSet.next()) {
                    val t: T = modelClass.getDeclaredConstructor().newInstance()
                    for (fieldName: String in fieldNames) {
                        val resultString: String = resultSet.getString(fieldName)
                        val setMethodName: String = generateSetMethodName(fieldName)
                        val method: Method = modelClass.getDeclaredMethod(setMethodName, String::class.java)
                        method.invoke(t, resultString)
                    }
                    list.add(t)
                }
            } catch (e: SQLException) {
                log.warn("get result list fail", e)
            } catch (e: ReflectiveOperationException) {
                log.warn("get result list fail", e)
            }
        }
        log.debug("select result size: " + list.size)
        return list
    }

    private fun generateGetMethodName(fieldName: String?): String {
        val name: String = StringUtils.underlineToHump(fieldName!!)
        val c: Char = name[0]
        val firstChar: String = c.toString()
        return "get" + firstChar.toUpperCase() + name.substring(1)
    }

    private fun generateSetMethodName(fieldName: String?): String {
        val name: String = StringUtils.underlineToHump(fieldName!!)
        val c: Char = name[0]
        val firstChar: String = c.toString()
        return "set" + firstChar.toUpperCase() + name.substring(1)
    }
}