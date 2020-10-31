package com.otakusaikou.simplerss.dao

import com.otakusaikou.simplerss.model.Feeds
import com.otakusaikou.simplerss.model.Subscriptions
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Feed(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Feed>(Feeds)

    var url by Feeds.url
    var context by Feeds.context
    var subscribers by Subscriber via Subscriptions
}