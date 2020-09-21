package com.onysakura.utils.basic

import com.onysakura.utils.log.CustomLogger

class StringUtils {

    companion object {
        private val LOG: CustomLogger.Log = CustomLogger.getLogger(StringUtils::class)

        /**
         * 驼峰转下划线
         */
        fun humpToUnderline(para: String): String {
            val sb = StringBuilder(para)
            var temp = 0
            if (!para.contains("_")) {
                for (i: Int in para.indices) {
                    if (Character.isUpperCase(para[i])) {
                        sb.insert(i + temp, "_")
                        temp += 1
                    }
                }
            }
            return sb.toString().toUpperCase()
        }

        /**
         * 下划线转驼峰
         */
        fun underlineToHump(para: String): String {
            val result = StringBuilder()
            val list: List<String> = para.split("_")
            for (s: String in list) {
                if (!para.contains("_")) {
                    result.append(s.substring(0, 1).toUpperCase())
                    result.append(s.substring(1).toLowerCase())
                    continue
                }
                if (result.isEmpty()) {
                    result.append(s.toLowerCase())
                } else {
                    result.append(s.substring(0, 1).toUpperCase())
                    result.append(s.substring(1).toLowerCase())
                }
            }
            return result.toString()
        }

        /**
         * 计算两个字符串的相似度
         */
        fun levenshtein(str1: String, str2: String): Int {
            //计算两个字符串的长度。
            val len1 = str1.length
            val len2 = str2.length
            //建立上面说的数组，比字符长度大一个空间
            val dif = Array(len1 + 1) { IntArray(len2 + 1) }
            for (a in 0..len1) {
                dif[a][0] = a
            }
            for (a in 0..len2) {
                dif[0][a] = a
            }
            //计算两个字符是否一样，计算左上的值
            var temp: Int
            for (i in 1..len1) {
                for (j in 1..len2) {
                    temp = if (str1[i - 1] == str2[j - 1]) {
                        0
                    } else {
                        1
                    }
                    //取三个值中最小的
                    dif[i][j] = MathUtils.min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1, dif[i - 1][j] + 1)
                }
            }
            LOG.debug("字符串\"$str1\"与\"$str2\"的比较")
            //取数组右下角的值，同样不同位置代表不同字符串的比较
            LOG.debug("差异步骤：" + dif[len1][len2])
            val v = 1 - dif[len1][len2].toFloat() / Math.max(str1.length, str2.length)
            LOG.debug("相似度：$v")
            //计算相似度
            return (v * 100).toInt()
        }

    }
}
