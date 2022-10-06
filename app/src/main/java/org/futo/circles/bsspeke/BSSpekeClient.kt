package org.futo.circles.bsspeke

import android.util.Base64


class BSSpekeClient(
    clientId: String,
    serverId: String,
    password: String
) {

    private val clientContext: Long = BSSpekeUtils.getClientContext()

    init {
        val rc = BSSpekeUtils.initClient(clientContext, clientId, serverId, password)
        if (rc != 0) throw BSSpekeError("Failed to initialize client (rc = $rc)")
    }

    fun generateBase64Blind(): String {
        val blind = ByteArray(32) { 0 }
        BSSpekeUtils.clientBlindSalt(blind, clientContext)
        return Base64.encodeToString(blind, Base64.NO_WRAP)
    }

    fun generatePandV(
        blindSalt: ByteArray,
        phfBlocks: Int,
        phfIterations: Int
    ): Pair<String, String> {
        val p = ByteArray(32) { 0 }
        val v = ByteArray(32) { 0 }
        assert(blindSalt.size == 32)
        val rc =
            BSSpekeUtils.generatePandV(p, v, blindSalt, phfBlocks, phfIterations, clientContext)

        if (rc != 0) throw BSSpekeError("Failed to generate permanent public key")

        return String(p) to String(v)
    }

    fun generateA(
        blindSalt: ByteArray,
        phfBlocks: Int,
        phfIterations: Int
    ): ByteArray {
        assert(blindSalt.size == 32)
        val rc = BSSpekeUtils.generateA(blindSalt, phfBlocks, phfIterations, clientContext)
        if (rc != 0) throw BSSpekeError("Failed to generate client ephemeral pubkey A")

        val a = ByteArray(32) { 0 }
        BSSpekeUtils.clientGetA(a, clientContext)
        return a
    }

    fun deriveSharedKey(B: ByteArray) {
        assert(B.size == 32)
        BSSpekeUtils.clientDeriveSharedKey(B, clientContext)
    }

    fun generateVerifier(): ByteArray {
        val verifier = ByteArray(32) { 0 }
        BSSpekeUtils.clientGenerateVerifier(verifier, clientContext)
        return verifier
    }

    fun verifyServer(verifier: ByteArray): Boolean {
        assert(verifier.size == 32)
        val rc = BSSpekeUtils.clientVerifyServer(verifier, clientContext)
        return rc == 0
    }

    fun generateHashKey(message: String): ByteArray {
        val k = ByteArray(32) { 0 }
        val messageByteArray = message.toByteArray()
        BSSpekeUtils.generateHashedKey(k, messageByteArray, clientContext)
        return k
    }
}