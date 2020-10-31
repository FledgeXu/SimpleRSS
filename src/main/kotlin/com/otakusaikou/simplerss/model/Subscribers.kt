package com.otakusaikou.simplerss.model

import org.jetbrains.exposed.dao.id.IntIdTable

object Subscribers : IntIdTable() {
    val qqNumber = integer("qq_number")
}