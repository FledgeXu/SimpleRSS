package com.otakusaikou.simplerss.service

import com.otakusaikou.simplerss.CONF
import com.otakusaikou.simplerss.SimpleRssConfSpec
import com.otakusaikou.simplerss.dao.Feed
import com.otakusaikou.simplerss.dao.Subscriber
import com.otakusaikou.simplerss.model.Feeds
import com.otakusaikou.simplerss.model.Subscribers
import com.otakusaikou.simplerss.model.Subscriptions
import org.apache.commons.io.FileUtils
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

const val SQLLITE_DRIVER: String = "org.sqlite.JDBC"
const val JDBC_HEAD: String = "jdbc:sqlite"

class FeedService() {
    init {
        val file = File(CONF[SimpleRssConfSpec.DATABASE_RELATIVE_PATH])
        if (!file.exists()) {
            FileUtils.touch(file)
        }
        Database.connect("$JDBC_HEAD:${file.absolutePath}", driver = SQLLITE_DRIVER)
    }

    fun initDatabase() {
        transaction {
            SchemaUtils.create(Feeds, Subscribers, Subscriptions)
        }
    }

    fun subFeed(feedUrl: String, qqGroup: Int) {
        transaction {
            val subscriber = if (Subscriber.find { Subscribers.qqNumber eq qqGroup }.empty())
                Subscriber.new { qqNumber = qqGroup }
            else
                Subscriber.find { Subscribers.qqNumber eq qqGroup }.first()
            val newFeed = if (Feed.find { Feeds.url eq feedUrl }.empty())
                Feed.new {
                    url = feedUrl
                    context = getFeedXML(url) ?: ""
                    time = System.currentTimeMillis() / 1000L // Unix Timestamp
                }
            else Feed.find { Feeds.url eq feedUrl }.first()
            subscriber.feeds = SizedCollection(subscriber.feeds.distinct() + newFeed)
        }
    }

    fun unsubFeed(feedUrl: String, qqGroup: Int) {
        transaction {
            if (!Subscriber.find { Subscribers.qqNumber eq qqGroup }.empty() && !Feed.find { Feeds.url eq feedUrl }.empty()) {
                val subscriber = Subscriber.find { Subscribers.qqNumber eq qqGroup }.first()
                subscriber.feeds = SizedCollection(subscriber.feeds.distinct().filter { it.url != feedUrl })
                val feed = Feed.find { Feeds.url eq feedUrl }.first()
                if (feed.subscribers.empty()) {
                    feed.delete()
                }
            }
        }
    }
}