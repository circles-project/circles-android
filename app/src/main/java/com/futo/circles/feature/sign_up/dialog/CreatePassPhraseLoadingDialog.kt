package com.futo.circles.feature.sign_up.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialog
import com.futo.circles.core.matrix.pass_phrase.CreatePassPhraseLoadingData
import com.futo.circles.databinding.CreatePassPhraseLoadingDialogBinding
import com.futo.circles.extensions.setVisibility


class CreatePassPhraseLoadingDialog(context: Context) : AppCompatDialog(context) {

    private val binding = CreatePassPhraseLoadingDialogBinding.inflate(LayoutInflater.from(context))

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    fun handleLoading(data: CreatePassPhraseLoadingData) {
        if (data.isLoading) {
            binding.tvLoadingMessage.setText(data.messageId)
            setProgress(data)
            if (isShowing.not()) show()
        } else {
            dismiss()
        }
    }

    private fun setProgress(data: CreatePassPhraseLoadingData) {
        with(binding) {
            horizontalProgress.max = data.total
            horizontalProgress.progress = data.progress
            horizontalProgress.setVisibility(data.total != data.progress)
        }

    }
}