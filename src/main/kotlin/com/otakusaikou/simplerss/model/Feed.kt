package com.otakusaikou.simplerss.model

import org.jetbrains.exposed.dao.id.IntIdTable

object Feed : IntIdTable() {
    val url = varchar("url", 2000)
    val context = text("context")
}