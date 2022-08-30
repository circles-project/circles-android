package org.futo.circles.bsspeke

class BSSpekeServerSession(
    serverId: String,
    clientId: String,
    private val salt: ByteArray
) {

    private val serverContext: Long = BSSpekeUtils.getServerContext()

    init {
        val rc = BSSpekeUtils.initServer(serverContext, clientId, serverId)
        if (rc != 0) throw BSSpekeError("Failed to initialize server (rc = $rc)")
    }

    fun blindSalt(blind: ByteArray): ByteArray {
        val blindSalt = ByteArray(32) { 0 }
        assert(blind.size == 32)
        BSSpekeUtils.serverBlindSalt(blind, blindSalt, salt)
        return blindSalt
    }

    fun generateB(basePoint: ByteArray): ByteArray {
        assert(basePoint.size == 32)
        BSSpekeUtils.generateB(basePoint, serverContext)
        val b = ByteArray(32) { 0 }
        BSSpekeUtils.generateB(b, serverContext)
        return b
    }

    fun deriveSharedKey(A: ByteArray, V: ByteArray) {
        assert(A.size == 32)
        assert(V.size == 32)
        BSSpekeUtils.serverDeriveSharedKey(A, V, serverContext)
    }

    fun generateVerifier(): ByteArray {
        val verifier = ByteArray(32) { 0 }
        BSSpekeUtils.serverGenerateVerifier(verifier, serverContext)
        return verifier
    }

    fun verifyClient(verifier: ByteArray): Boolean {
        assert(verifier.size == 32)
        val rc = BSSpekeUtils.serverVerifyClient(verifier, serverContext)
        return rc == 0
    }
}