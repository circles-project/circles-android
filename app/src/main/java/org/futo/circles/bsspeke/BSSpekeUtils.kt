package org.futo.circles.bsspeke

object BSSpekeUtils {

    init {
        System.loadLibrary("bsspeke")
    }

    external fun getServerContext(): Long
    external fun getClientContext(): Long

    external fun initClient(
        clientContext: Long,
        client_id_str: String,
        server_id_str: String,
        password_str: String
    )

    external fun initServer(serverContext: Long, client_id_str: String, server_id_str: String)

    external fun clientBlindSalt(blindByteArray: ByteArray, clientContext: Long)
    external fun serverBlindSalt(
        blindByteArray: ByteArray,
        blindSaltByteArray: ByteArray,
        saltByteArray: ByteArray
    )

    external fun generateB(PbyteArray: ByteArray, serverContext: Long)

}