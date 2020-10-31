package com.otakusaikou.simplerss.daemon

import com.otakusaikou.simplerss.CONF
import com.otakusaikou.simplerss.SimpleRssConfSpec
import com.otakusaikou.simplerss.service.FeedService
import java.lang.Thread.sleep

fun daemonEntry() {
    while (true) {
        sleep(CONF[SimpleRssConfSpec.FRESH_TIME] * 6000L)
        val feedService = FeedService()
        feedService.updateRSS()
        feedService.sendUpdateService()
    }
}