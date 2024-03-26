package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import org.futo.circles.R
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.databinding.ViewPostStatusBinding
import org.matrix.android.sdk.api.session.room.send.SendState

class PostStatusView(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {

    private val binding =
        ViewPostStatusBinding.inflate(LayoutInflater.from(context), this)

    init {
        orientation = HORIZONTAL
    }

    fun setIsEdited(isEdited: Boolean) {
        binding.tvEditedLabel.setIsVisible(isEdited)
    }

    fun setSendStatus(sendState: SendState, readByCount: Int) {
        when {
            sendState.isSending() -> {
                binding.ivSendStatus.setImageResource(R.drawable.ic_sending)
                binding.tvReadByCount.text = ""
            }

            sendState.hasFailed() -> {
                binding.ivSendStatus.setImageResource(R.drawable.ic_send_failed)
                binding.tvReadByCount.text = ""
            }

            sendState.isSent() -> {
                if (readByCount > 0) {
                    binding.ivSendStatus.setImageResource(org.futo.circles.core.R.drawable.ic_seen)
                    binding.tvReadByCount.text = readByCount.toString()
                } else {
                    binding.ivSendStatus.setImageResource(R.drawable.ic_sent)
                    binding.tvReadByCount.text = ""
                }
            }
        }
    }

}