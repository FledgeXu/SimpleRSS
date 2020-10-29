package com.otakusaikou.simplerss.frontend

import com.otakusaikou.simplerss.CONF
import com.otakusaikou.simplerss.SimpleRssConfSpec

fun frontEndEntry() {
    while (true) {
        Thread.sleep(1000)
        println("frontend")
        println(CONF[SimpleRssConfSpec.DATABASE_RELATIVE_PATH])
    }
}