package com.futo.circles.core.matrix.pass_phrase

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialog
import com.futo.circles.databinding.PassPhraseLoadingDialogBinding
import com.futo.circles.extensions.setIsVisible


class PassPhraseLoadingDialog(context: Context) : AppCompatDialog(context) {

    private val binding = PassPhraseLoadingDialogBinding.inflate(LayoutInflater.from(context))

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    fun handleLoading(data: PassPhraseLoadingData) {
        if (data.isLoading) {
            binding.tvLoadingMessage.setText(data.messageId)
            setProgress(data)
            if (isShowing.not()) show()
        } else {
            dismiss()
        }
    }

    private fun setProgress(data: PassPhraseLoadingData) {
        with(binding) {
            horizontalProgress.max = data.total
            horizontalProgress.progress = data.progress
            horizontalProgress.setIsVisible(data.total != data.progress)
        }

    }
}