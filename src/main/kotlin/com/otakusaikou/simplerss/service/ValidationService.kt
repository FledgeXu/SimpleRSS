package com.otakusaikou.simplerss.service

import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonException
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.otakusaikou.simplerss.CONF
import com.otakusaikou.simplerss.LOGGER
import com.otakusaikou.simplerss.SimpleRssConfSpec

class ValidationService() {

    fun auth(): String {
        LOGGER.info { "Begin Auth" }
        var authResult: String = ""

        data class AuthRequest(val authKey: String)

        data class AuthResponse(val code: Int, val session: String)

        postURL("${BASE_HTTP_URL}/auth", AuthRequest(CONF[SimpleRssConfSpec.AUTH_KEY])) { result ->
            try {
                val response = Klaxon().parse<AuthResponse>(result.get())
                if (response?.code == 0) {
                    authResult = response.session
                }
            } catch (e: KlaxonException) {
                LOGGER.info { result.get() }
            }

        }
        return authResult
    }

    fun verify(session: String): Boolean {
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

    fun postURL(url: String, requestObject: Any?, block: (r: Result<String, FuelError>) -> Unit) {
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
}