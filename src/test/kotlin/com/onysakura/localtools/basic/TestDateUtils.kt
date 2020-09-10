package com.onysakura.localtools.basic

import java.util.*
import kotlin.test.Test

class TestDateUtils {

    @Test
    fun format() {
        println(DateUtils.format(Date(), DateUtils.YYYY_MM_DD_HH_MM_SS))
        println(DateUtils.format(Date(), DateUtils.YYYYMMDDHHMMSS))
    }
}