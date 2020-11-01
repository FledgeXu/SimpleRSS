package com.otakusaikou.simplerss.frontend.command

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.otakusaikou.simplerss.LOGGER
import com.otakusaikou.simplerss.service.FeedService
import com.otakusaikou.simplerss.service.sendMessageService
import com.rometools.rome.io.SyndFeedInput
import java.io.IOException
import java.io.StringReader

//TODO: a command registry system.
object CommandParser {
    fun parser(rawText: String, qqGroup: Int) {
        if (rawText.isEmpty()) return
        if (rawText[0] != '/') {
            LOGGER.info { "\"${rawText}\" is not a Command" }
            return
        }
        val rawCommand = rawText.slice(1 until rawText.length)
        val commandWithParameter: List<String> = rawCommand.split(" ")
        when (commandWithParameter[0]) {
            "sub" -> {
                if (commandWithParameter.size != 2) {
                    sendMessageService("""
                        sub 命令参数错误
                        使用方法: /sub rssfeed
                    """.trimIndent(), qqGroup)
                    return
                }
                if (!subCommand(commandWithParameter[1], qqGroup)) {
                    sendMessageService("无效的URL", qqGroup)
                }
            }
            "unsub" -> {
                if (commandWithParameter.size != 2) {
                    sendMessageService("""
                        unsub 命令参数错误
                        使用方法: /unsub rssfeed
                    """.trimIndent(), qqGroup)
                    return
                }
                unsubCommand(commandWithParameter[1], qqGroup)
            }
            "echo" -> {
                sendMessageService(if (commandWithParameter.size > 2) commandWithParameter[1] else "", qqGroup)
            }
            else -> {
                sendMessageService("你输入的不是一个有效的命令，请检查命令格式", qqGroup)
            }
        }
    }

    private fun subCommand(feedUrl: String, qqGroup: Int): Boolean {
        if (!isValidFeed(feedUrl)) return false
        FeedService().subFeed(feedUrl, qqGroup)
        sendMessageService("你已经成功订阅：${feedUrl}", qqGroup)
        return true
    }

    private fun unsubCommand(feedUrl: String, qqGroup: Int) {
        FeedService().unsubFeed(feedUrl, qqGroup)
        sendMessageService("你已经取消订阅", qqGroup)
    }

    private fun isValidFeed(url: String): Boolean {
        var checkResult = false
        val feed = SyndFeedInput()
        try {
            val (_, _, result) = url.httpGet().responseString()
            when (result) {
                is Result.Success -> {
                    try {
                        feed.build(StringReader(result.value))
                        checkResult = true
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                else -> {
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return checkResult
    }
}