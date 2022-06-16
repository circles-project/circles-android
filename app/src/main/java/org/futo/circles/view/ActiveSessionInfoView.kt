package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import org.futo.circles.R
import org.futo.circles.databinding.ActiveSessionInfoViewBinding
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.feature.settings.active_sessions.list.ActiveSessionClickListener
import org.futo.circles.model.ActiveSession

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
            setIsCheckedView(
                ivCrossSigning,
                activeSession.cryptoDeviceInfo.trustLevel?.isCrossSigningVerified() == true
            )
            setIsCheckedView(
                ivLocalVerification,
                activeSession.cryptoDeviceInfo.trustLevel?.isLocallyVerified() == true
            )
        }
    }

    private fun setIsCheckedView(imageView: ImageView, isChecked: Boolean) {
        imageView.setImageResource(if (isChecked) R.drawable.ic_check else R.drawable.ic_close)
        imageView.setColorFilter(
            ContextCompat.getColor(
                context,
                if (isChecked) R.color.green else R.color.red
            )
        )
    }

}