package com.otakusaikou.simplerss.service

import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonException
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.otakusaikou.simplerss.CONF
import com.otakusaikou.simplerss.LOGGER
import com.otakusaikou.simplerss.SimpleRssConfSpec

class ValidationService() {

    fun auth(): String? {
        LOGGER.info { "Begin Auth" }
        var authResult: String? = null

        data class AuthRequest(val authKey: String)
        data class AuthResponse(val code: Int, val session: String)
        val (_, _, result) = "${BASE_URL}/auth"
                .httpPost()
                .jsonBody(Klaxon().toJsonString(AuthRequest(CONF[SimpleRssConfSpec.AUTH_KEY])))
                .responseString()
        when (result) {
            is Result.Success -> {
                try {
                    val response = Klaxon().parse<AuthResponse>(result.value)
                    if (response?.code == 0) {
                        authResult = response.session
                    }
                } catch (e: KlaxonException) {
                    LOGGER.info { result.value }
                }
            }
            else -> LOGGER.info { "Connect Fails" }
        }
        return authResult
    }
}