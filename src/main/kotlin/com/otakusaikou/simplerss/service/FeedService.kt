package com.otakusaikou.simplerss.service

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.otakusaikou.simplerss.CONF
import com.otakusaikou.simplerss.LOGGER
import com.otakusaikou.simplerss.SimpleRssConfSpec
import com.otakusaikou.simplerss.dao.Feed
import com.otakusaikou.simplerss.dao.Subscriber
import com.otakusaikou.simplerss.model.Feeds
import com.otakusaikou.simplerss.model.Subscribers
import com.otakusaikou.simplerss.model.Subscriptions
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.io.SyndFeedInput
import org.apache.commons.io.FileUtils
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.io.StringReader

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

    fun subFeed(feedUrl: String, qqGroup: Long) {
        transaction {
            val subscriber = if (Subscriber.find { Subscribers.qqNumber eq qqGroup }.empty())
                Subscriber.new { qqNumber = qqGroup }
            else
                Subscriber.find { Subscribers.qqNumber eq qqGroup }.first()
            val newFeed = if (Feed.find { Feeds.url eq feedUrl }.empty())
                Feed.new {
                    url = feedUrl
                    context = getFeedXML(url) ?: ""
                    oldContext = ""
                    time = System.currentTimeMillis() / 1000L // Unix Timestamp
                }
            else Feed.find { Feeds.url eq feedUrl }.first()
            subscriber.feeds = SizedCollection(subscriber.feeds.distinct() + newFeed)
        }
    }

    fun unsubFeed(feedUrl: String, qqGroup: Long) {
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

    fun updateRSS() {
        transaction {
            Feed.all().forEach {
                LOGGER.info { "Updating: ${it.url}" }
                val (_, _, result) = it.url.httpGet().responseString()
                when (result) {
                    is Result.Success -> {
                        it.oldContext = it.context
                        it.context = result.value
                        LOGGER.info { "Successful update: ${it.url}" }
                    }
                    else -> {
                        LOGGER.info { "${it.url} Connect Fails" }
                    }
                }
            }
        }
    }

    data class UpdateContext(val title: String, val url: String)

    fun sendUpdateService(): List<Pair<Int, List<UpdateContext>>> {
        var result: List<Pair<Int, List<UpdateContext>>> = mutableListOf()
        transaction {
            Feed.all().forEach {
                var contextList: List<UpdateContext> = mutableListOf()
                val oldItems = SyndFeedInput().build(StringReader(it.oldContext)).entries
                val newFeed = SyndFeedInput().build(StringReader(it.context))
                newFeed.entries.forEach { item ->
                    if (isNotInOldFeeds(item, oldItems)) {
                        LOGGER.info { item.title }
                        contextList = contextList + UpdateContext(item.title, item.link)
                    }
                }
                it.time = System.currentTimeMillis() / 1000L
                val pair: Pair<Int, List<UpdateContext>> = Pair(it.id.value, contextList)
                result = result + pair
            }
        }
        return result
    }

    fun sendUpdateMessage() {
        val updates: List<Pair<Int, List<UpdateContext>>> = sendUpdateService()
        transaction {
            updates.forEach { update ->
                val feed = Feed.findById(update.first)
                feed?.subscribers?.forEach { subscriber ->
                    update.second.forEach { updateContext ->
                        if (updateContext.title != "") {
                            sendMessageService("${updateContext.title}\n${updateContext.url}", subscriber.qqNumber)
                        }
                    }
                }
            }
        }
    }

    fun getSubList(qqGroup: Long): List<String> {
        var result: List<String> = mutableListOf()
        transaction {
            val subscriber = Subscriber.find { Subscribers.qqNumber eq qqGroup }.first()
            subscriber.feeds.forEach {
                result = result + it.url
            }
        }
        return result
    }

    private fun isNotInOldFeeds(item: SyndEntry, oldItems: List<SyndEntry>): Boolean {
        oldItems.forEach {
            if (item.title == it.title) {
                return false
            }
        }
        return true
    }
}