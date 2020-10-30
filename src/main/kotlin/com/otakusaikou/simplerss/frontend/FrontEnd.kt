package com.otakusaikou.simplerss.frontend

import com.otakusaikou.simplerss.service.BASE_WEBSOCKET_URL
import com.otakusaikou.simplerss.service.ValidationService
import okhttp3.OkHttpClient
import okhttp3.Request


class FrontEnd() {
    var session: String

    init {
        val validationService = ValidationService()
        session = validationService.auth()
        validationService.verify(session)
    }


    fun frontEndEntry() {
        val client = OkHttpClient()
        val request: Request = Request.Builder().url("${BASE_WEBSOCKET_URL}/message?sessionKey=${session}").build()
        val listener = MessageListener()
        client.newWebSocket(request, listener)
        client.dispatcher.executorService.shutdown()
    }
}