package com.onysakura.localtools.log

import com.onysakura.localtools.basic.DateUtils
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.logging.*
import java.util.logging.Formatter
import kotlin.reflect.KClass

class CustomLogger {

    companion object {
        private const val CLASS_NAME_LENGTH_LIMIT = 30
        private const val IS_SAVE_LOG_FILE = true
        private val LOG_FILE_LEVEL: Level = Level.SEVERE
        private val LOG_CONSOLE_LEVEL: Level = Level.INFO
        private val path: Path = Paths.get("logs")
        private var fileHandler: FileHandler? = null
        private var formatter: Formatter = object : Formatter() {
            override fun format(record: LogRecord): String {
                return (getColor(record.level) +
                        DateUtils.format(Date(record.millis), DateUtils.YYYY_MM_DD_HH_MM_SS) + " "
                        + "[" + String.format("%5s", (record.level)) + "] "
                        + getShortClassName(record.loggerName) + ": "
                        + record.message + "\u001b[0m\n")
            }
        }

        init {
            if (IS_SAVE_LOG_FILE) {
                try {
                    val logFilePath: String = path.toFile().absolutePath + "/" + DateUtils.format(Date(), DateUtils.YYYYMMDDHHMMSS) + ".log"
                    val file = File(logFilePath)
                    file.mkdirs()
                    fileHandler = FileHandler(logFilePath)
                    fileHandler?.formatter = formatter
                    fileHandler?.level = LOG_FILE_LEVEL
                } catch (ignored: IOException) {
                }
            }
        }

        fun getColor(level: Level): String {
            return when (level.name) {
                "SEVERE" -> "\u001b[31m"
                "WARNING" -> "\u001b[33m"
                "INFO" -> "\u001b[30m"
                "FINE", "FINER", "FINEST" -> "\u001b[37m"
                else -> "DEBUG"
            }
        }

        fun getLogger(loggerName: String): Log {
            val logger: Logger = Logger.getLogger(loggerName)
            logger.useParentHandlers = false
            logger.level = Level.ALL
            val consoleHandler = ConsoleHandler()
            consoleHandler.formatter = formatter
            consoleHandler.level = LOG_CONSOLE_LEVEL
            logger.addHandler(consoleHandler)
            if (IS_SAVE_LOG_FILE && fileHandler != null) {
                logger.addHandler(fileHandler)
            }
            return Log(logger)
        }

        fun getLogger(clazz: Class<*>): Log {
            return getLogger(clazz.simpleName)
        }

        fun getLogger(kClass: KClass<*>): Log {
            return getLogger(kClass.toString())
        }

        private fun getShortClassName(className: String): String? {
            return if (className.length <= CLASS_NAME_LENGTH_LIMIT) {
                String.format("%" + CLASS_NAME_LENGTH_LIMIT + "s", className)
            } else String.format("%" + CLASS_NAME_LENGTH_LIMIT + "s", className.substring(className.lastIndexOf(".") + 1))
        }

        fun getLevel(level: Level): String {
            return when (level.name) {
                "SEVERE" -> "ERROR";
                "WARNING" -> "WARN";
                "INFO" -> "INFO";
                "FINE", "FINER", "FINEST" -> "DEBUG";
                else -> "DEBUG";
            }
        }
    }

    open class Log(private val logger: Logger) {
        fun debug(msg: Any) {
            logger.fine(msg.toString())
        }

        fun info(msg: Any) {
            logger.info(msg.toString())
        }

        fun warn(msg: Any) {
            logger.warning(msg.toString())
        }

        fun warn(msg: Any, exception: Exception) {
            logger.warning(msg.toString() + "\n" + getException(exception))
        }


        fun error(msg: Any) {
            logger.severe(msg.toString())
        }

        fun error(msg: Any, exception: Exception) {
            logger.severe(msg.toString() + "\n" + getException(exception))
        }

        fun getException(exception: Exception): String {
            val stringWriter = StringWriter();
            val printWriter = PrintWriter(stringWriter)
            exception.printStackTrace(printWriter)
            return stringWriter.toString()
        }
    }
}