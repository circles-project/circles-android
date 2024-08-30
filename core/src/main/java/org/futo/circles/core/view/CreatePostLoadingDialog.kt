package org.futo.circles.core.view

import android.app.ActionBar
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialog
import org.futo.circles.core.databinding.DialogCreatePostLoadingBinding
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.model.LoadingData
import org.futo.circles.core.model.MessageLoadingData
import org.futo.circles.core.model.ResLoadingData

class CreatePostLoadingDialog(context: Context) : AppCompatDialog(context) {

    private val binding = DialogCreatePostLoadingBinding.inflate(LayoutInflater.from(context))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.apply {
            setDimAmount(0.8f)
            setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT)
        }
    }

    fun handleLoading(data: LoadingData) {
        if (data.isLoading) {
            setProgress(data)
            if (isShowing.not()) show()
        } else {
            dismiss()
        }
    }

    fun setProgress(data: LoadingData) {
        with(binding) {
            tvLoadingMessage.text = when (data) {
                is MessageLoadingData -> data.message
                is ResLoadingData -> context.getString(data.messageId)
            }
            horizontalProgress.apply {
                setIsVisible(data.total != data.progress)
                max = data.total
                progress = data.progress
            }
        }
    }
}