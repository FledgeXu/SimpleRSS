package com.otakusaikou.simplerss.daemon

import java.lang.Thread.sleep

fun daemonEntry() {
    while (true){
        sleep(1000)
        println("deamon")
    }
}