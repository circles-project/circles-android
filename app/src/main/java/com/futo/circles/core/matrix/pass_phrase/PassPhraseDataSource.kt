package com.futo.circles.core.matrix.pass_phrase

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.futo.circles.R
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.crypto.crosssigning.CrossSigningService
import org.matrix.android.sdk.api.session.crypto.crosssigning.MASTER_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.SELF_SIGNING_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.crypto.crosssigning.USER_SIGNING_KEY_SSSS_NAME
import org.matrix.android.sdk.api.session.securestorage.EmptyKeySigner
import org.matrix.android.sdk.api.session.securestorage.SharedSecretStorageService
import org.matrix.android.sdk.api.session.securestorage.SsssKeyCreationInfo
import org.matrix.android.sdk.internal.crypto.keysbackup.model.rest.KeysVersionResult
import org.matrix.android.sdk.internal.util.awaitCallback
import java.util.*

class PassPhraseDataSource(private val context: Context) {

    private val session by lazy { MatrixSessionProvider.currentSession }
    private val loadingLiveData = MutableLiveData<PassPhraseLoadingEvent>()

    private suspend fun doesKeyBackupExist() {
        val version = awaitCallback<KeysVersionResult?> {
            session?.cryptoService()?.keysBackupService()?.getCurrentVersion(it)
        }

        val keyVersion = awaitCallback<KeysVersionResult?> {
            session?.cryptoService()?.keysBackupService()?.getVersion(version?.version ?: "", it)
        }

        if (keyVersion != null) {
            isBackupCreatedFromPassPhrase(keyVersion)
        }

    }

    private fun isBackupCreatedFromPassPhrase(keyVersion: KeysVersionResult): Boolean =
        keyVersion.getAuthDataAsMegolmBackupAuthData()?.privateKeySalt != null


    private suspend fun generateSecretFromPassPhrase(passphrase: String) {
        val storageService = session?.sharedSecretStorageService ?: return
        val crossSigningService = session?.cryptoService()?.crossSigningService() ?: return

        loadingLiveData.postValue(LoadingPassPhrase(R.string.generating_passphrase))

        val keyInfo = storageService.generateKeyWithPassphrase(
            UUID.randomUUID().toString(),
            keyName,
            passphrase,
            EmptyKeySigner(),
            null
        )

        setDefaultKey(storageService, keyInfo.keyId)
        storePrivateSecrets(storageService, crossSigningService,keyInfo)

    }

    private suspend fun setDefaultKey(storageService: SharedSecretStorageService, keyId: String) {
        loadingLiveData.postValue(LoadingPassPhrase(R.string.defining_default_key))
        storageService.setDefaultKey(keyId)
    }

    private suspend fun storePrivateSecrets(
        storageService: SharedSecretStorageService,
        crossSigningService: CrossSigningService,
        keyInfo: SsssKeyCreationInfo
    ) {
        val missingKeyException = IllegalArgumentException(context.getString(R.string.missing_private_key))
        val keyRef = listOf(SharedSecretStorageService.KeyRef(keyInfo.keyId, keyInfo.keySpec))
        val xKeys = crossSigningService.getCrossSigningPrivateKeys()

        val mskPrivateKey = xKeys?.master ?: throw missingKeyException
        loadingLiveData.postValue(LoadingPassPhrase(R.string.synchronizing_master_key))
        storageService.storeSecret(MASTER_KEY_SSSS_NAME, mskPrivateKey, keyRef)

        val uskPrivateKey = xKeys.user ?: throw missingKeyException
        loadingLiveData.postValue(LoadingPassPhrase(R.string.synchronizing_user_key))
        storageService.storeSecret(USER_SIGNING_KEY_SSSS_NAME, uskPrivateKey, keyRef)

        val sskPrivateKey = xKeys.selfSigned ?: throw missingKeyException
        loadingLiveData.postValue(LoadingPassPhrase(R.string.synchronizing_self_key))
        storageService.storeSecret(SELF_SIGNING_KEY_SSSS_NAME, sskPrivateKey, keyRef)
    }

    companion object {
        private const val keyName = "ssss_key"
    }
}