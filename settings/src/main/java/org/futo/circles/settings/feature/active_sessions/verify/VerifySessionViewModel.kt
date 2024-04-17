package org.futo.circles.settings.feature.active_sessions.verify

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.futo.circles.auth.model.QrLoading
import org.futo.circles.auth.model.QrReady
import org.futo.circles.auth.model.QrState
import org.futo.circles.auth.model.QrSuccess
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.crypto.verification.EVerificationState
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

    val qrStateLiveData: MutableLiveData<QrState> = MutableLiveData(QrLoading)
    private var qrTransactionId: String = ""

    init {
        observeVerificationState()
        initVerification()
    }

    private fun observeVerificationState() {
        session.cryptoService().verificationService().requestEventFlow()
            .cancellable()
            .onEach {
                when (it) {
                    is VerificationEvent.RequestAdded -> confirmIncomingRequest(it.request)

                    is VerificationEvent.RequestUpdated -> {
                        if (it.request.state == EVerificationState.Done) {
                            qrStateLiveData.postValue(QrSuccess)
                        } else {
                            qrTransactionId = it.transactionId
                            it.request.qrCodeText?.let { qrStateLiveData.postValue(QrReady(it)) }
                        }
                    }

                    is VerificationEvent.TransactionAdded -> transactionUpdated(it.transaction)
                    is VerificationEvent.TransactionUpdated -> transactionUpdated(it.transaction)
                }
            }.flowOn(Dispatchers.IO).launchIn(viewModelScope)
    }


    private fun transactionUpdated(tx: VerificationTransaction) {
        val transaction = (tx as? QrCodeVerificationTransaction) ?: return
        if (transaction.state() == QRCodeVerificationState.WaitingForScanConfirmation)
            launchBg { transaction.otherUserScannedMyQrCode() }
    }

    fun onQrScanned(data: String) {
        launchBg {
            session.cryptoService().verificationService().reciprocateQRVerification(
                session.myUserId, qrTransactionId, data
            )
        }
    }

    private fun initVerification() {
        launchBg {
            if (session.cryptoService().crossSigningService().isCrossSigningVerified()) {
                requestKeyVerification()
            } else {
                val request = session.cryptoService().verificationService()
                    .getExistingVerificationRequests(session.myUserId).lastOrNull { it.isIncoming }
                request?.let { confirmIncomingRequest(it) }
            }
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