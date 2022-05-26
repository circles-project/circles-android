package com.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.futo.circles.databinding.ActiveSessionInfoViewBinding
import com.futo.circles.extensions.setIsVisible
import com.futo.circles.feature.settings.active_sessions.list.ActiveSessionClickListener
import com.futo.circles.model.ActiveSession

class ActiveSessionInfoView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding = ActiveSessionInfoViewBinding.inflate(LayoutInflater.from(context), this)

    private var activeSessionClickListener: ActiveSessionClickListener? = null
    private var deviceId: String? = null

    init {
        binding.btnRemove.setOnClickListener {
            deviceId?.let { activeSessionClickListener?.onRemoveSessionClicked(it) }
        }
        binding.btnVerify.setOnClickListener {
            deviceId?.let { activeSessionClickListener?.onVerifySessionClicked(it) }
        }
    }

    fun setData(
        activeSession: ActiveSession,
        listener: ActiveSessionClickListener
    ) {
        deviceId = activeSession.id
        activeSessionClickListener = listener
        with(binding) {
            tvFingerprint.text = activeSession.cryptoDeviceInfo.fingerprint() ?: ""
            tvPublicKey.text = activeSession.cryptoDeviceInfo.identityKey() ?: ""
            btnVerify.setIsVisible(!activeSession.cryptoDeviceInfo.isVerified && !activeSession.isCurrentSession())
            btnRemove.setIsVisible(!activeSession.isCurrentSession())
        }
    }

}