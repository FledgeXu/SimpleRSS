package com.otakusaikou.simplerss.model

import org.jetbrains.exposed.dao.id.IntIdTable

object Subscriber : IntIdTable() {
    val qqNumber = long("qq_number")
}