package org.futo.circles.feature.settings.active_sessions.bootstrap

import android.content.Context
import org.futo.circles.R
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.crypto.crosssigning.MASTER_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.SELF_SIGNING_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.USER_SIGNING_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.isVerified
import org.matrix.android.sdk.api.session.securestorage.KeyInfoResult
import org.matrix.android.sdk.api.session.securestorage.RawBytesKeySpec
import org.matrix.android.sdk.api.util.awaitCallback

class CrossSigningDataSource(private val context: Context) {

    suspend fun initCrossSigning() {
        val session = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
            context.getString(R.string.session_is_not_created)
        )
        awaitCallback {
            session.cryptoService().crossSigningService().initializeCrossSigning(null, it)
        }
    }


    suspend fun configureCrossSigning(recoveryKey: String) {
        val session = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
            context.getString(R.string.session_is_not_created)
        )
        val keyId = (session.sharedSecretStorageService()
            .getDefaultKey() as? KeyInfoResult.Success)?.keyInfo?.id
        val keySpec =
            RawBytesKeySpec.fromRecoveryKey(recoveryKey) ?: throw IllegalArgumentException(
                context.getString(R.string.backup_could_not_be_decrypted_with_key)
            )

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

}