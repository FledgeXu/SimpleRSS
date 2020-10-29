package com.otakusaikou.simplerss.service

import com.otakusaikou.simplerss.CONF
import com.otakusaikou.simplerss.SimpleRssConfSpec

val BASE_URL: String by lazy {
    "http://${CONF[SimpleRssConfSpec.IP_ADDRESS]}:${CONF[SimpleRssConfSpec.PORT]}"
}