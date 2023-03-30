package org.futo.circles.feature.settings.active_sessions.bootstrap

import android.content.Context
import org.futo.circles.R
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.crosssigning.MASTER_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.SELF_SIGNING_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.USER_SIGNING_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.isVerified
import org.matrix.android.sdk.api.session.securestorage.KeyInfoResult
import org.matrix.android.sdk.api.session.securestorage.KeyRef
import org.matrix.android.sdk.api.session.securestorage.SsssKeySpec
import org.matrix.android.sdk.api.util.awaitCallback

class CrossSigningDataSource(private val context: Context) {

    suspend fun initCrossSigning(keySpec: SsssKeySpec) {
        val session = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
            context.getString(R.string.session_is_not_created)
        )
        val crossSigningService = session.cryptoService().crossSigningService()
        if (!crossSigningService.isCrossSigningInitialized())
            awaitCallback { crossSigningService.initializeCrossSigning(null, it) }
        storeKeys(session, keySpec)
    }

    suspend fun configureCrossSigning(keySpec: SsssKeySpec) {
        val session = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
            context.getString(R.string.session_is_not_created)
        )
        val keyId = (session.sharedSecretStorageService()
            .getDefaultKey() as? KeyInfoResult.Success)?.keyInfo?.id

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
            awaitCallback {
                session.sessionParams.deviceId?.let { deviceId ->
                    session.cryptoService().crossSigningService().trustDevice(deviceId, it)
                }
            }
        }
    }

    private suspend fun storeKeys(session: Session, keySpec: SsssKeySpec) {
        val xKeys = session.cryptoService().crossSigningService().getCrossSigningPrivateKeys()
        val mskPrivateKey = xKeys?.master
            ?: throw IllegalArgumentException(context.getString(R.string.key_is_missing))
        val sskPrivateKey = xKeys.selfSigned
            ?: throw IllegalArgumentException(context.getString(R.string.key_is_missing))
        val uskPrivateKey = xKeys.user
            ?: throw IllegalArgumentException(context.getString(R.string.key_is_missing))

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