package com.onysakura.localtools.db.sqlite

import org.testng.Reporter
import kotlin.test.BeforeTest
import kotlin.test.Test

class TestSQLite {

    @BeforeTest
    fun before() {
        SQLite.init("C:/Files/Workspaces/Mine/LocalTools/src/main/resources/sqlite.db")
    }

    @Test
    fun basic() {
        println(SQLite::class.qualifiedName)
    }

    @Test
    fun dropTable() {
        SQLite.execute("drop table TEST;")
    }

    @Test
    fun createTable() {
        SQLite.execute("CREATE TABLE IF NOT EXISTS TEST (ID TEXT PRIMARY KEY NOT NULL, AAA TEXT);")
    }

    @Test
    fun insert() {

    }

}