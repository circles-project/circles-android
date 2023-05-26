package org.futo.circles.auth.bsspeke

object BSSpekeUtils {

    init {
        System.loadLibrary("bsspeke")
    }

    external fun getClientContext(): Long

    external fun initClient(
        clientContext: Long,
        client_id_str: String,
        server_id_str: String,
        password_str: String
    ): Int

    external fun clientBlindSalt(blindByteArray: ByteArray, clientContext: Long)

    external fun generatePandV(
        PbyteArray: ByteArray, VbyteArray: ByteArray,
        blindSaltByteArray: ByteArray,
        phfBlocks: Int,
        phfIterations: Int,
        clientContext: Long
    ): Int

    external fun generateA(
        blindSaltByteArray: ByteArray, phfBlocks: Int,
        phfIterations: Int,
        clientContext: Long
    ): Int

    external fun clientDeriveSharedKey(BbyteArray: ByteArray, clientContext: Long)

    external fun clientGenerateVerifier(clientVerifierByteArray: ByteArray, clientContext: Long)

    external fun clientVerifyServer(clientVerifierByteArray: ByteArray, clientContext: Long): Int

    external fun clientGetA(AbyteArray: ByteArray, clientContext: Long)

    external fun generateHashedKey(
        KbyteArray: ByteArray,
        messageByteArray: ByteArray,
        clientContext: Long
    )
}