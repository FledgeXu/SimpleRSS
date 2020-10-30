package com.otakusaikou.simplerss.service

import com.otakusaikou.simplerss.CONF
import com.otakusaikou.simplerss.SimpleRssConfSpec

val BASE_HTTP_URL: String by lazy {
    "http://${BASE_URL}"
}
val BASE_WEBSOCKET_URL: String by lazy {
    "ws://${BASE_URL}"
}
val BASE_URL: String by lazy {
    "${CONF[SimpleRssConfSpec.IP_ADDRESS]}:${CONF[SimpleRssConfSpec.PORT]}"
}