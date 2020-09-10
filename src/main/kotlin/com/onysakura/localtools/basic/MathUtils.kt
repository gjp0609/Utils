package com.onysakura.localtools.basic

class MathUtils {
    companion object {
        /**
         * 得到最小值
         */
        fun min(vararg int: Int): Int {
            var min: Int = Int.MAX_VALUE
            for (i: Int in int) {
                if (min > i) {
                    min = i
                }
            }
            return min
        }
    }
}