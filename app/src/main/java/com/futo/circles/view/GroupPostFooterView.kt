package com.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.futo.circles.R
import com.futo.circles.databinding.GroupPostFooterViewBinding
import com.futo.circles.extensions.getAttributes
import com.futo.circles.extensions.setEnabledChildren
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
            val isEnabled = getBoolean(R.styleable.GroupPostFooterView_android_enabled, true)
            setEnabledChildren(isEnabled)
        }
    }

    fun setData(data: PostInfo, isReply: Boolean) {
        with(binding) {
            btnReply.setIsVisible(!isReply)
            ivEncrypted.setIsEncryptedIcon(data.isEncrypted)
            tvMessageTime.text = DateFormat.getDateTimeInstance().format(Date(data.timestamp))
        }
    }

}