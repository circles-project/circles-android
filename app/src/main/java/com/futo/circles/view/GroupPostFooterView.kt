package com.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.futo.circles.R
import com.futo.circles.databinding.GroupPostFooterViewBinding
import com.futo.circles.extensions.getAttributes
import com.futo.circles.extensions.setIsEncryptedIcon
import com.futo.circles.extensions.setIsVisible
import com.futo.circles.model.PostInfo
import java.text.DateFormat
import java.util.*


class GroupPostFooterView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        GroupPostFooterViewBinding.inflate(LayoutInflater.from(context), this)

    init {
        getAttributes(attrs, R.styleable.GroupPostFooterView) {
            val optionsVisible = getBoolean(R.styleable.GroupPostFooterView_optionsVisible, true)
            binding.optionsGroup.setIsVisible(optionsVisible)
        }
    }

    fun setData(data: PostInfo, isReply: Boolean) {
        setData(data.timestamp, data.isEncrypted, isReply)
    }

    fun setData(timestamp: Long, isEncrypted: Boolean, isReply: Boolean) {
        with(binding) {
            btnReply.setIsVisible(!isReply)
            ivEncrypted.setIsEncryptedIcon(isEncrypted)
            tvMessageTime.text = DateFormat.getDateTimeInstance().format(Date(timestamp))
        }
    }

}