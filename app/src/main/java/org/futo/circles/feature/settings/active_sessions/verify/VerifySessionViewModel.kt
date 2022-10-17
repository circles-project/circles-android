package org.futo.circles.feature.settings.active_sessions.verify

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import org.futo.circles.R
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.crypto.verification.*

class VerifySessionViewModel(
    private val deviceId: String,
    context: Context
) : ViewModel(), VerificationService.Listener {

    private val session = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
        context.getString(R.string.session_is_not_created)
    )

    init {
        Log.d("MyLog", "init $deviceId")
        session.cryptoService().verificationService().addListener(this)
    }

    override fun verificationRequestCreated(pr: PendingVerificationRequest) {
        Log.d("MyLog", "v create")
    }

    override fun verificationRequestUpdated(pr: PendingVerificationRequest) {
        Log.d("MyLog", "v update")
    }


    override fun transactionCreated(tx: VerificationTransaction) {
        Log.d("MyLog", "tx created $tx")
    }


    override fun transactionUpdated(tx: VerificationTransaction) {
        Log.d("MyLog", "tx update $tx")
    }

    override fun markedAsManuallyVerified(userId: String, deviceId: String) {
        Log.d("MyLog", "manually $userId")
    }

    fun requestVerification() {
        val a = session.cryptoService().verificationService().requestKeyVerification(
            listOf(VerificationMethod.QR_CODE_SCAN),
            session.myUserId,
            listOf(deviceId)
        )
        session.cryptoService().verificationService().readyPendingVerification(
            listOf(VerificationMethod.QR_CODE_SCAN),
            session.myUserId,
            a.transactionId ?: ""
        )
    }

    override fun onCleared() {
        session.cryptoService().verificationService().removeListener(this)
        super.onCleared()
    }
}