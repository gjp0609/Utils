package com.onysakura.utils.basic

class MathUtils {

    companion object {
        /**
         * 得到数组 [intArray] 中的最小值
         */
        fun min(vararg intArray: Int): Int {
            var min: Int = Int.MAX_VALUE
            for (i: Int in intArray) {
                if (min > i) {
                    min = i
                }
            }
            return min
        }
    }
}

fun main() {
    println(MathUtils.min(3,5,6,7,7))
}