package org.futo.circles.bsspeke

object BSSpekeUtils {

    init {
        System.loadLibrary("bsspeke")
    }

    external fun getServerContext(): Long
    external fun getClientContext(): Long

    external fun initClient(clientContext: Long, client_id: String, server_id: String, password: String)
    external fun initServer(clientContext: Long, client_id: String, server_id: String)

}