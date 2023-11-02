package org.futo.circles.auth.feature.active_sessions.verify

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.futo.circles.auth.model.QrCanceled
import org.futo.circles.auth.model.QrLoading
import org.futo.circles.auth.model.QrReady
import org.futo.circles.auth.model.QrState
import org.futo.circles.auth.model.QrSuccess
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.crypto.verification.PendingVerificationRequest
import org.matrix.android.sdk.api.session.crypto.verification.QRCodeVerificationState
import org.matrix.android.sdk.api.session.crypto.verification.QrCodeVerificationTransaction
import org.matrix.android.sdk.api.session.crypto.verification.VerificationEvent
import org.matrix.android.sdk.api.session.crypto.verification.VerificationMethod
import org.matrix.android.sdk.api.session.crypto.verification.VerificationTransaction
import javax.inject.Inject

@HiltViewModel
class VerifySessionViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val deviceId: String = savedStateHandle.getOrThrow("deviceId")

    private val session = MatrixSessionProvider.getSessionOrThrow()

    private val verificationMethods = listOf(
        VerificationMethod.QR_CODE_SHOW,
        VerificationMethod.QR_CODE_SCAN
    )

    val qrStateLiveData: MutableLiveData<QrState> = MutableLiveData(QrLoading(deviceId))

    private var qrTransaction: QrCodeVerificationTransaction? = null

    init {
        session.cryptoService().verificationService().requestEventFlow()
            .cancellable()
            .onEach {
                when (it) {
                    is VerificationEvent.RequestAdded -> confirmIncomingRequest(it.request)
                    is VerificationEvent.RequestUpdated -> confirmIncomingRequest(it.request)

                    is VerificationEvent.TransactionAdded -> transactionUpdated(it.transaction)
                    is VerificationEvent.TransactionUpdated -> transactionUpdated(it.transaction)
                }
            }.launchIn(viewModelScope)

        initVerification()
    }


    private fun transactionUpdated(tx: VerificationTransaction) {
        val transaction = (tx as? QrCodeVerificationTransaction) ?: return
        when (transaction.state()) {
            QRCodeVerificationState.Reciprocated -> launchBg { qrTransaction?.otherUserScannedMyQrCode() }
            QRCodeVerificationState.Done -> qrStateLiveData.postValue(QrSuccess)
            QRCodeVerificationState.Cancelled -> qrStateLiveData.postValue(QrCanceled)
            else -> {
                qrTransaction = transaction
                qrTransaction?.qrCodeText?.let { qrStateLiveData.postValue(QrReady(it)) }
            }
        }
    }

    override fun onCleared() {
        qrTransaction = null
        super.onCleared()
    }

    fun onQrScanned(data: String) {
        launchBg {
            session.cryptoService().verificationService().reciprocateQRVerification(
                qrTransaction?.otherUserId ?: "",
                qrTransaction?.transactionId ?: "",
                data
            )
        }
    }

    private fun initVerification() {
        launchBg {
            if (session.cryptoService().crossSigningService().isCrossSigningVerified())
                requestKeyVerification()
        }
    }

    private fun confirmIncomingRequest(request: PendingVerificationRequest) = launchBg {
        session.cryptoService().verificationService().readyPendingVerification(
            verificationMethods,
            request.otherUserId,
            request.transactionId
        )
    }

    private suspend fun requestKeyVerification() {
        session.cryptoService().verificationService().requestDeviceVerification(
            verificationMethods, session.myUserId, deviceId
        )
    }
}