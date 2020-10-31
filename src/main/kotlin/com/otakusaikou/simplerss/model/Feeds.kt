package com.otakusaikou.simplerss.model

import org.jetbrains.exposed.dao.id.IntIdTable

object Feeds : IntIdTable() {
    val url = varchar("url", 2000)
    val context = text("context")
    val oldContext = text("old_context")
    val time = long("timestamps")
}