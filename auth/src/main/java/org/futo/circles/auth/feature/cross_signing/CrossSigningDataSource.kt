package org.futo.circles.auth.feature.cross_signing

import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.crosssigning.MASTER_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.SELF_SIGNING_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.USER_SIGNING_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.isVerified
import org.matrix.android.sdk.api.session.securestorage.KeyInfoResult
import org.matrix.android.sdk.api.session.securestorage.KeyRef
import org.matrix.android.sdk.api.session.securestorage.SsssKeySpec
import javax.inject.Inject

class CrossSigningDataSource @Inject constructor() {

    suspend fun initCrossSigningIfNeed(keySpec: SsssKeySpec) {
        val session = MatrixSessionProvider.getSessionOrThrow()
        val crossSigningService = session.cryptoService().crossSigningService()
        try {
            session.sharedSecretStorageService().getSecret(MASTER_KEY_SSSS_NAME, null, keySpec)
        } catch (ignore: Throwable) {
            tryOrNull { crossSigningService.initializeCrossSigning(null) }
            storeKeys(session, keySpec)
        }
    }

    suspend fun configureCrossSigning(keySpec: SsssKeySpec) {
        val session = MatrixSessionProvider.getSessionOrThrow()
        val keyId = (session.sharedSecretStorageService()
            .getDefaultKey() as? KeyInfoResult.Success)?.keyInfo?.id

        initCrossSigningIfNeed(keySpec)

        val ssssService = session.sharedSecretStorageService()
        val mskPrivateKey =
            ssssService.getSecret(MASTER_KEY_SSSS_NAME, keyId, keySpec)
        val uskPrivateKey =
            ssssService.getSecret(USER_SIGNING_KEY_SSSS_NAME, keyId, keySpec)
        val sskPrivateKey =
            ssssService.getSecret(SELF_SIGNING_KEY_SSSS_NAME, keyId, keySpec)

        val trustResult =
            session.cryptoService().crossSigningService().checkTrustFromPrivateKeys(
                mskPrivateKey, uskPrivateKey, sskPrivateKey
            )
        if (trustResult.isVerified()) {
            tryOrNull {
                session.cryptoService().crossSigningService()
                    .trustDevice(session.sessionParams.deviceId)
            }
        }
    }

    private suspend fun storeKeys(session: Session, keySpec: SsssKeySpec) {
        val xKeys = session.cryptoService().crossSigningService().getCrossSigningPrivateKeys()
        val mskPrivateKey = xKeys?.master
            ?: throw IllegalArgumentException("The key is missing")
        val sskPrivateKey = xKeys.selfSigned
            ?: throw IllegalArgumentException("The key is missing")
        val uskPrivateKey = xKeys.user
            ?: throw IllegalArgumentException("The key is missing")

        val keyId = (session.sharedSecretStorageService()
            .getDefaultKey() as? KeyInfoResult.Success)?.keyInfo?.id

        session.sharedSecretStorageService().storeSecret(
            MASTER_KEY_SSSS_NAME,
            mskPrivateKey,
            listOf(KeyRef(keyId, keySpec))
        )
        session.sharedSecretStorageService().storeSecret(
            USER_SIGNING_KEY_SSSS_NAME,
            uskPrivateKey,
            listOf(KeyRef(keyId, keySpec))
        )
        session.sharedSecretStorageService().storeSecret(
            SELF_SIGNING_KEY_SSSS_NAME,
            sskPrivateKey,
            listOf(KeyRef(keyId, keySpec))
        )
    }

}