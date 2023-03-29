package org.futo.circles.feature.settings.active_sessions.bootstrap

import android.content.Context
import org.futo.circles.R
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.crypto.crosssigning.MASTER_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.SELF_SIGNING_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.USER_SIGNING_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.isVerified
import org.matrix.android.sdk.api.session.securestorage.SsssKeyCreationInfo
import org.matrix.android.sdk.api.util.awaitCallback

class CrossSigningDataSource(private val context: Context) {

    suspend fun initCrossSigning(){
        val session = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
            context.getString(R.string.session_is_not_created)
        )
        awaitCallback {
            session.cryptoService().crossSigningService().initializeCrossSigning(null, it)
        }
    }

    suspend fun configureCrossSigning(keyInfo: SsssKeyCreationInfo) {
        val session = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
            context.getString(R.string.session_is_not_created)
        )
        val ssssService = session.sharedSecretStorageService()
        val mskPrivateKey =
            ssssService.getSecret(MASTER_KEY_SSSS_NAME, keyInfo.keyId, keyInfo.keySpec)
        val uskPrivateKey =
            ssssService.getSecret(USER_SIGNING_KEY_SSSS_NAME, keyInfo.keyId, keyInfo.keySpec)
        val sskPrivateKey =
            ssssService.getSecret(SELF_SIGNING_KEY_SSSS_NAME, keyInfo.keyId, keyInfo.keySpec)

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

}