package org.futo.circles.auth.bsspeke

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

    fun generateBase64PandV(
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

        return Base64.encodeToString(p, Base64.NO_WRAP) to Base64.encodeToString(v, Base64.NO_WRAP)
    }

    fun generateABase64(
        blindSalt: ByteArray,
        phfBlocks: Int,
        phfIterations: Int
    ): String {
        assert(blindSalt.size == 32)
        val rc = BSSpekeUtils.generateA(blindSalt, phfBlocks, phfIterations, clientContext)
        if (rc != 0) throw BSSpekeError("Failed to generate client ephemeral pubkey A")

        val a = ByteArray(32) { 0 }
        BSSpekeUtils.clientGetA(a, clientContext)
        return Base64.encodeToString(a, Base64.NO_WRAP)
    }

    fun deriveSharedKey(B: ByteArray) {
        assert(B.size == 32)
        BSSpekeUtils.clientDeriveSharedKey(B, clientContext)
    }

    fun generateVerifierBase64(): String {
        val verifier = ByteArray(32) { 0 }
        BSSpekeUtils.clientGenerateVerifier(verifier, clientContext)
        return Base64.encodeToString(verifier, Base64.NO_WRAP)
    }

    fun generateHashKey(message: String): ByteArray {
        val k = ByteArray(32) { 0 }
        val messageByteArray = message.toByteArray()
        BSSpekeUtils.generateHashedKey(k, messageByteArray, clientContext)
        return k
    }
}