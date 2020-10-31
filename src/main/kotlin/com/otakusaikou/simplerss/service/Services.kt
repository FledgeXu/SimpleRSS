package com.otakusaikou.simplerss.service

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.beust.klaxon.Parser
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.otakusaikou.simplerss.CONF
import com.otakusaikou.simplerss.LOGGER
import com.otakusaikou.simplerss.SimpleRssConfSpec
import com.otakusaikou.simplerss.session
import org.jetbrains.exposed.sql.transactions.transaction


fun authService(): String {
    LOGGER.info { "Begin Auth" }
    var authResult: String = ""

    data class AuthRequest(val authKey: String)


    postURL("${BASE_HTTP_URL}/auth", AuthRequest(CONF[SimpleRssConfSpec.AUTH_KEY])) { result ->
        val response: JsonObject = Parser.default().parse(StringBuilder(result.get())) as JsonObject
        if (response["code"] == 0) {
            authResult = response["session"] as String
        } else {
            LOGGER.info { result.get() }
        }
    }
    return authResult
}

fun verifyService(session: String): Boolean {
    var verifyResult = false
    LOGGER.info { "Begin verify" }
    data class VerityRequest(val sessionKey: String, val qq: Int)
    data class VerityResponse(val code: Int, val msg: String)
    postURL("${BASE_HTTP_URL}/verify", VerityRequest(session, CONF[SimpleRssConfSpec.BOT_QQ])) { result ->
        val response = Klaxon().parse<VerityResponse>(result.get())
        if (response?.code == 0) {
            verifyResult = true
        } else {
            LOGGER.info { result.get() }
        }
    }
    return verifyResult
}

//TODO: We can abstract it with #authService.
fun sendMessageService(message: String, qq: Int): Boolean {
    data class PlainMessage(val type: String, val text: String)
    data class Message(val sessionKey: String, val group: Int, val messageChain: List<PlainMessage>)

    var sendMessageResult: Boolean = false
    postURL("${BASE_HTTP_URL}/sendGroupMessage", Message(session, qq, listOf(PlainMessage("Plain", message)))) { result ->
        val response: JsonObject = Parser.default().parse(StringBuilder(result.get())) as JsonObject
        if (response["code"] == 0) {
            sendMessageResult = true;
        } else {
            LOGGER.info { result.get() }
        }
    }
    return sendMessageResult
}

fun getFeedXML(url: String): String? {
    val (_, _, result) = url.httpGet().responseString()
    return when (result) {
        is Result.Success -> {
            result.value
        }
        else -> null
    }
}


private fun postURL(url: String, requestObject: Any?, block: (r: Result<String, FuelError>) -> Unit) {
    val (_, _, result) = url
            .httpPost()
            .jsonBody(Klaxon().toJsonString(requestObject))
            .responseString()
    when (result) {
        is Result.Success -> {
            block(result)
        }
        else -> LOGGER.info { "Connect Fails" }
    }
}