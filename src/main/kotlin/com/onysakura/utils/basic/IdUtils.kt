package com.onysakura.utils.basic

import com.onysakura.utils.log.CustomLogger
import java.util.*

class IdUtils {

    companion object {
        private val LOG: CustomLogger.Log = CustomLogger.getLogger(IdUtils::class)
        private val SNOWFLAKE_ID_WORKER: SnowflakeIdWorker = SnowflakeIdWorker(0, 0)

        /**
         * 获取 snowflake id
         */
        fun getNextId(): String {
            val nextId: Long = SNOWFLAKE_ID_WORKER.nextId()
            return nextId.toString()
        }

        fun getUUID(): String {
            return UUID.randomUUID().toString()
        }

        fun getUUIDWithoutLine(): String {
            return getUUID().replace("-", "")
        }

    }
}