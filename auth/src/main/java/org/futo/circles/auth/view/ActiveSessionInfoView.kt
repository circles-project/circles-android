package org.futo.circles.auth.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.futo.circles.auth.databinding.ViewActiveSessionInfoBinding
import org.futo.circles.auth.feature.active_sessions.list.ActiveSessionClickListener
import org.futo.circles.auth.model.ActiveSession
import org.futo.circles.core.extensions.setIsVisible

class ActiveSessionInfoView(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding = ViewActiveSessionInfoBinding.inflate(LayoutInflater.from(context), this)

    private var activeSessionClickListener: ActiveSessionClickListener? =
        null
    private var deviceId: String? = null

    init {
        binding.btnRemove.setOnClickListener {
            deviceId?.let { activeSessionClickListener?.onRemoveSessionClicked(it) }
        }
        binding.btnVerify.setOnClickListener {
            deviceId?.let { activeSessionClickListener?.onVerifySessionClicked(it) }
        }
        binding.btnResetKeys.setOnClickListener {
            activeSessionClickListener?.onResetKeysClicked()
        }
    }

    fun setData(
        activeSession: ActiveSession,
        listener: ActiveSessionClickListener
    ) {
        deviceId = activeSession.id
        activeSessionClickListener = listener
        with(binding) {
            vLoading.setIsVisible(activeSession.isLoading)
            tvFingerprint.text = activeSession.cryptoDeviceInfo.fingerprint() ?: ""
            tvPublicKey.text = activeSession.cryptoDeviceInfo.identityKey() ?: ""
            btnVerify.setIsVisible(activeSession.canVerify && !activeSession.isLoading)
            btnRemove.setIsVisible(!activeSession.isCurrentSession() && !activeSession.isLoading)
            btnResetKeys.setIsVisible(activeSession.isResetKeysVisible && !activeSession.isLoading)
        }
    }
}