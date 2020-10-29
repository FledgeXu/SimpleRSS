package com.otakusaikou.simplerss

import com.otakusaikou.simplerss.daemon.daemonEntry
import com.otakusaikou.simplerss.frontend.frontEndEntry
import com.otakusaikou.simplerss.service.ValidationService
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.toml
import com.uchuhimo.konf.source.toml.toToml
import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import java.io.File
import kotlin.concurrent.thread

//const val SQLLITE_DRIVER: String = "org.sqlite.JDBC"
//const val JDBC_HEAD: String = "jdbc:sqlite"
const val CONF_PATH = "data/conf.toml"
val LOGGER = KotlinLogging.logger {}
lateinit var CONF: Config
fun main() {
    LOGGER.info { "Starting" }
    initFile()
    val v = ValidationService()
    thread(isDaemon = true) {
        daemonEntry()
    }
    frontEndEntry()
}

fun initFile() {
    LOGGER.info { "Prepare Files" }
    configInit()
    databaseFileInit()
}

fun databaseFileInit() {
    LOGGER.info { "Create Database file" }
    FileUtils.touch(File(CONF[SimpleRssConfSpec.DATABASE_RELATIVE_PATH]))
}

fun configInit() {
    LOGGER.info { "Config Init" }
    val configFile = File(CONF_PATH)
    FileUtils.touch(configFile)
    CONF = Config { addSpec(SimpleRssConfSpec) }
            .from.toml.watchFile(CONF_PATH)
    CONF.toToml.toFile(configFile)
}
