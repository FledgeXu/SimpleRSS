package com.otakusaikou.simplerss.frontend

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.otakusaikou.simplerss.LOGGER
import com.otakusaikou.simplerss.frontend.command.CommandParser
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class MessageListener : WebSocketListener() {
    private val _normalClosureStatus = 1000

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(_normalClosureStatus, null)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        LOGGER.info { t.printStackTrace() }

    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        LOGGER.info { text }
        val json: JsonObject = Parser.default().parse(StringBuilder(text)) as JsonObject
        if (json["type"] == "GroupMessage") {
            LOGGER.info { json.obj("sender")?.string("permission") }
            val permission = json.obj("sender")?.string("permission")
            if (permission == "ADMINISTRATOR" || permission == "OWNER") {
                json.array<JsonObject>("messageChain")?.forEach {
                    CommandParser.parser(if (it["text"] != null) it["text"] as String else "",
                            // We have checked "GroupMessage" here and it should not be null. If it is, it's not our failure and we should let it crash.
                            qqGroup = json.obj("sender")?.obj("group")?.get("id") as Int)
                }
            }
        }
    }
}