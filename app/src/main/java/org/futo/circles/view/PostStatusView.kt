package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.databinding.ViewPostStatusBinding

class PostStatusView(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {

    private val binding =
        ViewPostStatusBinding.inflate(LayoutInflater.from(context), this)

    init {
        orientation = HORIZONTAL
        gravity = Gravity.END
    }

    fun setIsEdited(isEdited: Boolean) {
        binding.tvEditedLabel.setIsVisible(isEdited)
    }

    fun setReadByCount(readByCount: Int) {
        if (readByCount > 0) {
            binding.ivSendStatus.setImageResource(org.futo.circles.core.R.drawable.ic_seen)
            binding.tvReadByCount.text = readByCount.toString()
        } else {
            binding.ivSendStatus.setImageDrawable(null)
            binding.tvReadByCount.text = ""
        }
    }

}