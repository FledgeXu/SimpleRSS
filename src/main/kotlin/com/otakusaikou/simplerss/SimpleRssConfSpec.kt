package com.otakusaikou.simplerss

import com.uchuhimo.konf.ConfigSpec

object SimpleRssConfSpec : ConfigSpec("Config") {
    val DATABASE_RELATIVE_PATH by optional("data/database.sqlite", "database", "database location")
    val IP_ADDRESS by optional("127.0.0.1", "ip_address", "Mirai http api")
    val PORT by optional(8777, "port", "Mirai http port")
    val AUTH_KEY by optional("123456789", "auth_key", "Mirai http authKey")
    val BOT_QQ by optional(1111111111, "bot_qq", "The QQ number of RSS Bot")
    val FRESH_TIME by optional(10, "fresh_time", "When limitation of fetch rss feed")
}