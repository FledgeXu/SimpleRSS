package com.otakusaikou.simplerss.model

import org.jetbrains.exposed.dao.id.IntIdTable

object Subscription : IntIdTable() {
    val qqId = integer("qq_id")
    val urlId = integer("url_id")
}