package org.futo.circles.bsspeke

object BSSpekeUtils {

    init {
        System.loadLibrary("bsspeke")
    }

    external fun getServerContext(): Long
    external fun getClientContext(): Long

}