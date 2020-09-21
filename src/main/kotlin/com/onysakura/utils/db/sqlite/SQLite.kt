package com.onysakura.utils.db.sqlite

import com.onysakura.utils.log.CustomLogger
import java.io.File
import java.sql.*

class SQLite {
    companion object {
        private val LOG: CustomLogger.Log = CustomLogger.getLogger(SQLite::class)

        init {
            LOG.debug("SQLite init")
            try {
                Class.forName("org.sqlite.JDBC")
                LOG.debug("open database successfully")
            } catch (e: Exception) {
                LOG.error("open database fail", e)
            }
        }

        private var c: Connection? = null
        private var stmt: Statement? = null

        fun init(path: String) {
            c = DriverManager.getConnection("jdbc:sqlite:" + File(path).absolutePath)
            stmt = c?.createStatement()
        }

        fun executeQuery(sql: String): ResultSet? {
            if (c == null || stmt == null) throw RuntimeException("sqlite is not init")
            LOG.debug("executeQuery: $sql")
            return try {
                c!!.autoCommit = false
                val resultSet = stmt!!.executeQuery(sql)
                c!!.commit()
                LOG.debug("executeQuery has resultSet: " + (resultSet != null))
                resultSet
            } catch (e: SQLException) {
                LOG.warn("executeUpdate sql fail, sql: $sql", e)
                null
            }
        }

        fun executeUpdate(sql: String): Int {
            if (c == null || stmt == null) throw RuntimeException("sqlite is not init")
            LOG.debug("executeUpdate: $sql")
            return try {
                c!!.autoCommit = false
                val execute: Int = stmt!!.executeUpdate(sql)
                c!!.commit()
                LOG.debug("executeUpdate result: $execute")
                execute
            } catch (e: SQLException) {
                LOG.warn("executeUpdate sql fail, sql: $sql", e)
                -1
            }
        }

        fun execute(sql: String): Boolean {
            if (c == null || stmt == null) throw RuntimeException("sqlite is not init")
            LOG.debug("execute: $sql")
            var success = false
            try {
                c!!.autoCommit = false
                val execute: Boolean = stmt!!.execute(sql)
                if (execute) {
                    success = true
                    c!!.commit()
                }
                LOG.debug("execute result: $execute")
            } catch (e: SQLException) {
                LOG.warn("execute sql fail, sql: $sql", e)
            }
            return success
        }

        fun escape(keyWord: String): String {
            return keyWord.replace("'", "''")
        }

    }
}