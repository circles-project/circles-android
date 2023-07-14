package org.futo.circles.settings.feature.active_sessions.verify

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.settings.model.QrCanceled
import org.futo.circles.settings.model.QrLoading
import org.futo.circles.settings.model.QrReady
import org.futo.circles.settings.model.QrState
import org.futo.circles.settings.model.QrSuccess
import org.matrix.android.sdk.api.session.crypto.verification.PendingVerificationRequest
import org.matrix.android.sdk.api.session.crypto.verification.QrCodeVerificationTransaction
import org.matrix.android.sdk.api.session.crypto.verification.VerificationMethod
import org.matrix.android.sdk.api.session.crypto.verification.VerificationService
import org.matrix.android.sdk.api.session.crypto.verification.VerificationTransaction
import org.matrix.android.sdk.api.session.crypto.verification.VerificationTxState
import javax.inject.Inject

@HiltViewModel
class VerifySessionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel(), VerificationService.Listener {

    private val deviceId: String = savedStateHandle.getOrThrow("deviceId")

    private val session = MatrixSessionProvider.getSessionOrThrow()

    private val verificationMethods = listOf(
        VerificationMethod.QR_CODE_SHOW,
        VerificationMethod.QR_CODE_SCAN
    )

    val qrStateLiveData: MutableLiveData<QrState> = MutableLiveData(
        QrLoading(
            deviceId,
            session.cryptoService().crossSigningService().isCrossSigningVerified()
        )
    )

    private var qrTransaction: QrCodeVerificationTransaction? = null

    init {
        session.cryptoService().verificationService().addListener(this)
        initVerification()
    }

    override fun verificationRequestCreated(pr: PendingVerificationRequest) {
        verificationRequestUpdated(pr)
    }

    override fun verificationRequestUpdated(pr: PendingVerificationRequest) {
        confirmIncomingRequest()
    }

    override fun transactionCreated(tx: VerificationTransaction) {
        transactionUpdated(tx)
    }

    override fun transactionUpdated(tx: VerificationTransaction) {
        when (val state = tx.state) {
            is VerificationTxState.Cancelled -> qrStateLiveData.postValue(QrCanceled(state.cancelCode.humanReadable))
            VerificationTxState.Verified -> qrStateLiveData.postValue(QrSuccess)
            VerificationTxState.QrScannedByOther -> qrTransaction?.otherUserScannedMyQrCode()
            else -> {
                qrTransaction = tx as? QrCodeVerificationTransaction
                qrTransaction?.qrCodeText?.let { qrStateLiveData.postValue(QrReady(it)) }
            }
        }
    }

    override fun onCleared() {
        qrTransaction?.cancel()
        session.cryptoService().verificationService().removeListener(this)
        super.onCleared()
    }

    fun onQrScanned(data: String) {
        qrTransaction?.userHasScannedOtherQrCode(data)
    }

    private fun initVerification() {
        if (session.cryptoService().crossSigningService().isCrossSigningVerified())
            requestKeyVerification()
        else confirmIncomingRequest()
    }

    private fun confirmIncomingRequest() {
        session.cryptoService().verificationService()
            .getExistingVerificationRequests(session.myUserId)
            .lastOrNull { it.isIncoming && !it.isReady }?.let {
                session.cryptoService().verificationService()
                    .readyPendingVerification(
                        verificationMethods,
                        it.otherUserId,
                        it.transactionId ?: ""
                    )
            }
    }

    private fun requestKeyVerification() {
        session.cryptoService().verificationService().requestKeyVerification(
            verificationMethods,
            session.myUserId,
            listOf(deviceId)
        )
    }
}