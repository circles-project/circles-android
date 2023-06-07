package org.futo.circles.feature.settings.active_sessions.verify

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.R
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.model.*
import org.matrix.android.sdk.api.session.crypto.verification.*
import javax.inject.Inject

@HiltViewModel
class VerifySessionViewModel @Inject constructor(
    private val deviceId: String,
    @ApplicationContext context: Context
) : ViewModel(), VerificationService.Listener {

    private val session = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
        context.getString(R.string.session_is_not_created)
    )

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

    @Suppress("KotlinConstantConditions")
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