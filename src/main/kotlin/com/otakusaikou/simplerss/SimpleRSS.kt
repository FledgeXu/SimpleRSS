package com.otakusaikou.simplerss

import com.otakusaikou.simplerss.daemon.daemonEntry
import com.otakusaikou.simplerss.frontend.FrontEnd
import com.otakusaikou.simplerss.service.FeedService
import com.otakusaikou.simplerss.service.authService
import com.otakusaikou.simplerss.service.verifyService
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.toml
import com.uchuhimo.konf.source.toml.toToml
import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import java.io.File
import kotlin.concurrent.thread

const val CONF_PATH = "data/conf.toml"
val LOGGER = KotlinLogging.logger {}
lateinit var CONF: Config
lateinit var session: String
fun main() {
    LOGGER.info { "Starting" }
    initFile()
    session = intSession()
    thread(isDaemon = true) {
        daemonEntry()
    }
    FrontEnd().frontEndEntry()
}

fun initFile() {
    LOGGER.info { "Prepare Files" }
    configInit()
    databaseFileInit()
}

fun intSession(): String {
    val session = authService()
    verifyService(session)
    return session
}

fun databaseFileInit() {
    LOGGER.info { "Create Database file" }
    FeedService().initDatabase()
}

fun configInit() {
    LOGGER.info { "Config Init" }
    val configFile = File(CONF_PATH)
    FileUtils.touch(configFile)
    CONF = Config { addSpec(SimpleRssConfSpec) }
            .from.toml.watchFile(CONF_PATH)
    CONF.toToml.toFile(configFile)
}
