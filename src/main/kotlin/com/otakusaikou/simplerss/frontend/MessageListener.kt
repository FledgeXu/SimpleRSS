package com.otakusaikou.simplerss.frontend

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.otakusaikou.simplerss.LOGGER
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
        LOGGER.info { json["type"] }
    }
}