package com.otakusaikou.simplerss.dao

import com.otakusaikou.simplerss.model.Subscribers
import com.otakusaikou.simplerss.model.Subscriptions
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class Subscriber(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Subscriber>(Subscribers)

    var qqNumber by Subscribers.qqNumber
    var feeds by Feed via Subscriptions
}