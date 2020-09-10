package com.onysakura.localtools.db.sqlite


class TestSQLite {

    companion object {
        fun basic() {
            SQLite.toString()
        }

        fun init(){
            SQLite.init("C:/Files/Workspaces/Mine/LocalTools/src/main/resources/sqlite.db")
        }
    }
}

fun main() {
    TestSQLite.basic()
}