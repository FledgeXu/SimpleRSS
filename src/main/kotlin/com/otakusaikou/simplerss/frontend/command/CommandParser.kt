package com.otakusaikou.simplerss.frontend.command

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.otakusaikou.simplerss.LOGGER
import com.otakusaikou.simplerss.service.sendMessageService
import java.io.IOException

//TODO: a command registry system.
object CommandParser {
    fun parser(rawText: String, qqGroup: Int) {
        if (rawText.isEmpty()) return;
        if (rawText[0] != '/') {
            LOGGER.info { "\"${rawText}\" is not a Command" }
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
            }
            "echo" -> {
                sendMessageService(if (commandWithParameter.size > 2) commandWithParameter[1] else "", qqGroup)
            }
            else -> {

            }
        }
    }

    private fun subCommand(url: String, qqGroup: Int): Boolean {
        if (!isValidFeed(url)) return false
        return true
    }

    private fun isValidFeed(url: String): Boolean {
        var checkResult = false
        try {
            val (_, _, result) = url.httpGet().responseString()
            when (result) {
                is Result.Success -> {
                    checkResult = true
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