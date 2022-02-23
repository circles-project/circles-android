package com.futo.circles.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.futo.circles.databinding.GroupPostFooterViewBinding
import com.futo.circles.extensions.setIsEncryptedIcon
import java.text.DateFormat
import java.util.*


class GroupPostFooterView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        GroupPostFooterViewBinding.inflate(LayoutInflater.from(context), this)

    fun setData(isEncrypted: Boolean, timestamp: Long) {
        binding.ivEncrypted.setIsEncryptedIcon(isEncrypted)
        binding.tvMessageTime.text = DateFormat.getDateTimeInstance().format(Date(timestamp))
    }

}