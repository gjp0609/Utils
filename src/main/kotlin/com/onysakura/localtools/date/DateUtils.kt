package com.onysakura.localtools.date

import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object {
        const val YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
        const val YYYYMMDDHHMMSS = "yyyyMMddHHmmss"

        fun format(date: Date, patten: String): String {
            val format = SimpleDateFormat(patten)
            return format.format(date)
        }
    }
}