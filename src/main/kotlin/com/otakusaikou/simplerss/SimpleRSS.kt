package com.otakusaikou.simplerss

import com.otakusaikou.simplerss.daemon.daemonEntry
import com.otakusaikou.simplerss.frontend.FrontEnd
import com.otakusaikou.simplerss.model.Feeds
import com.otakusaikou.simplerss.model.Subscribers
import com.otakusaikou.simplerss.model.Subscriptions
import com.otakusaikou.simplerss.service.authService
import com.otakusaikou.simplerss.service.verifyService
import com.uchuhimo.konf.Config
import com.uchuhimo.konf.source.toml
import com.uchuhimo.konf.source.toml.toToml
import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import kotlin.concurrent.thread

const val SQLLITE_DRIVER: String = "org.sqlite.JDBC"
const val JDBC_HEAD: String = "jdbc:sqlite"
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
    val file = File(CONF[SimpleRssConfSpec.DATABASE_RELATIVE_PATH])
    if (!file.exists()) {
        FileUtils.touch(file)
    }
    Database.connect("${JDBC_HEAD}:${file.absolutePath}", driver = SQLLITE_DRIVER, user = "root", password = "")
    transaction {
        LOGGER.info { "Database schema init" }
        SchemaUtils.create(Feeds, Subscribers, Subscriptions)
    }

}

fun configInit() {
    LOGGER.info { "Config Init" }
    val configFile = File(CONF_PATH)
    FileUtils.touch(configFile)
    CONF = Config { addSpec(SimpleRssConfSpec) }
            .from.toml.watchFile(CONF_PATH)
    CONF.toToml.toFile(configFile)
}
