package com.otakusaikou.simplerss.model

import org.jetbrains.exposed.dao.id.IntIdTable

object Subscriptions : IntIdTable() {
    val subscriber = reference("subscriber", Subscribers)
    val feed = reference("feed", Feeds)
}