package org.futo.circles.feature.settings.active_sessions

import org.futo.circles.R
import org.futo.circles.feature.settings.active_sessions.bootstrap.BootstrapCrossSigningParams
import org.futo.circles.feature.settings.active_sessions.bootstrap.BootstrapProgressListener
import org.futo.circles.feature.settings.active_sessions.bootstrap.BootstrapResult
import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.MatrixError
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.crypto.crosssigning.*
import org.matrix.android.sdk.api.session.crypto.keysbackup.*
import org.matrix.android.sdk.api.session.securestorage.EmptyKeySigner
import org.matrix.android.sdk.api.session.securestorage.KeyRef
import org.matrix.android.sdk.api.session.securestorage.SsssKeyCreationInfo
import org.matrix.android.sdk.api.util.awaitCallback
import org.matrix.android.sdk.api.util.toBase64NoPadding
import java.util.*

class BootstrapCrossSigningDataSource {

    suspend fun bootstrapCrossSigning(
        session: Session,
        params: BootstrapCrossSigningParams
    ): BootstrapResult {
        val crossSigningService = session.cryptoService().crossSigningService()
        initializeCrossSigningIfNeeded(
            crossSigningService,
            params.userInteractiveAuthInterceptor,
            params.progressListener
        )

        val ssssService = session.sharedSecretStorageService()
        params.progressListener?.onProgress(R.string.bootstrap_crosssigning_progress_pbkdf2)

        val keyInfo: SsssKeyCreationInfo
        try {
            keyInfo = params.passphrase?.let { passphrase ->
                ssssService.generateKeyWithPassphrase(
                    UUID.randomUUID().toString(),
                    "ssss_key",
                    passphrase,
                    EmptyKeySigner(),
                    null
                )
            } ?: run {
                ssssService.generateKey(
                    UUID.randomUUID().toString(),
                    params.keySpec,
                    "ssss_key",
                    EmptyKeySigner()
                )
            }
        } catch (failure: Failure) {
            return BootstrapResult.FailedToCreateSSSSKey(failure)
        }

        params.progressListener?.onProgress(R.string.bootstrap_crosssigning_progress_default_key)
        try {
            ssssService.setDefaultKey(keyInfo.keyId)
        } catch (failure: Failure) {
            return BootstrapResult.FailedToSetDefaultSSSSKey(failure)
        }

        val xKeys = crossSigningService.getCrossSigningPrivateKeys()
        val mskPrivateKey = xKeys?.master ?: return BootstrapResult.MissingPrivateKey
        val sskPrivateKey = xKeys.selfSigned ?: return BootstrapResult.MissingPrivateKey
        val uskPrivateKey = xKeys.user ?: return BootstrapResult.MissingPrivateKey

        try {
            params.progressListener?.onProgress(R.string.bootstrap_crosssigning_progress_save_msk)
            ssssService.storeSecret(
                MASTER_KEY_SSSS_NAME,
                mskPrivateKey,
                listOf(KeyRef(keyInfo.keyId, keyInfo.keySpec))
            )
            params.progressListener?.onProgress(R.string.bootstrap_crosssigning_progress_save_usk)
            ssssService.storeSecret(
                USER_SIGNING_KEY_SSSS_NAME,
                uskPrivateKey,
                listOf(KeyRef(keyInfo.keyId, keyInfo.keySpec))
            )
            params.progressListener?.onProgress(R.string.bootstrap_crosssigning_progress_save_ssk)
            ssssService.storeSecret(
                SELF_SIGNING_KEY_SSSS_NAME,
                sskPrivateKey,
                listOf(KeyRef(keyInfo.keyId, keyInfo.keySpec))
            )
        } catch (failure: Failure) {
            return BootstrapResult.FailedToStorePrivateKeyInSSSS(failure)
        }

        params.progressListener?.onProgress(R.string.bootstrap_crosssigning_progress_key_backup)
        try {
            // First ensure that in sync
            val serverVersion = awaitCallback<KeysBackupLastVersionResult> {
                session.cryptoService().keysBackupService().getCurrentVersion(it)
            }.toKeysVersionResult()
            val knownMegolmSecret =
                session.cryptoService().keysBackupService().getKeyBackupRecoveryKeyInfo()
            val isMegolmBackupSecretKnown =
                knownMegolmSecret != null && knownMegolmSecret.version == serverVersion?.version

            // ensure we store existing backup secret if we have it!
            if (isMegolmBackupSecretKnown) {
                // check it matches
                val isValid = awaitCallback<Boolean> {
                    session.cryptoService().keysBackupService()
                        .isValidRecoveryKeyForCurrentVersion(
                            knownMegolmSecret!!.recoveryKey,
                            it
                        )
                }
                if (isValid) {
                    extractCurveKeyFromRecoveryKey(knownMegolmSecret!!.recoveryKey)?.toBase64NoPadding()
                        ?.let { secret ->
                            ssssService.storeSecret(
                                KEYBACKUP_SECRET_SSSS_NAME,
                                secret,
                                listOf(KeyRef(keyInfo.keyId, keyInfo.keySpec))
                            )
                        }
                }
            }
        } catch (ignore: Throwable) {
        }
        return BootstrapResult.Success(keyInfo)
    }

    private suspend fun initializeCrossSigningIfNeeded(
        crossSigningService: CrossSigningService,
        userInteractiveAuthInterceptor: UserInteractiveAuthInterceptor?,
        progressListener: BootstrapProgressListener?
    ): BootstrapResult {
        val shouldSetCrossSigning = !crossSigningService.isCrossSigningInitialized()
        if (shouldSetCrossSigning) {
            progressListener?.onProgress(R.string.bootstrap_crosssigning_progress_initializing)
            try {
                awaitCallback<Unit> {
                    crossSigningService.initializeCrossSigning(userInteractiveAuthInterceptor, it)
                }
            } catch (failure: Throwable) {
                if (failure is Failure.ServerError && failure.error.code == MatrixError.M_FORBIDDEN) {
                    return BootstrapResult.InvalidPasswordError(failure.error)
                }
                return BootstrapResult.GenericError(failure)
            }
        }
    }
}