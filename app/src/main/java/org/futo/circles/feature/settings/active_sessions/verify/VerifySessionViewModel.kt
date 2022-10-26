package org.futo.circles.feature.settings.active_sessions.verify

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.R
import org.futo.circles.model.*
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.crypto.verification.*

class VerifySessionViewModel(
    private val deviceId: String,
    context: Context
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
            session.cryptoService().crossSigningService().isCrossSigningInitialized()
        )
    )

    private var qrTransaction: QrCodeVerificationTransaction? = null

    init {
        session.cryptoService().verificationService().addListener(this)
        requestVerification()
    }

    override fun verificationRequestCreated(pr: PendingVerificationRequest) {
        verificationRequestUpdated(pr)
    }

    override fun verificationRequestUpdated(pr: PendingVerificationRequest) {
        pr.cancelConclusion?.let {
            qrStateLiveData.postValue(QrCanceled(it.humanReadable))
            return
        }
        if(pr.isSuccessful){
            qrStateLiveData.postValue(QrSuccess)
            return
        }

        if (pr.isIncoming && !pr.isReady) {
            session.cryptoService().verificationService()
                .readyPendingVerification(
                    verificationMethods,
                    pr.otherUserId,
                    pr.transactionId ?: ""
                )
        }
    }


    override fun transactionCreated(tx: VerificationTransaction) {
        transactionUpdated(tx)
    }

    override fun transactionUpdated(tx: VerificationTransaction) {
        qrTransaction = tx as? QrCodeVerificationTransaction
        qrTransaction?.qrCodeText?.let { qrStateLiveData.postValue(QrReady(it)) }
    }

    private fun requestVerification() {
        session.cryptoService().verificationService().requestKeyVerification(
            verificationMethods,
            session.myUserId,
            listOf(deviceId)
        )
    }

    override fun onCleared() {
        session.cryptoService().verificationService().removeListener(this)
        super.onCleared()
    }

    fun onQrScanned(data: String) {
        qrTransaction?.userHasScannedOtherQrCode(data)
    }
}