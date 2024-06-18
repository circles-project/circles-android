package org.futo.circles.core.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialog
import org.futo.circles.core.databinding.DialogLoadingBinding
import org.futo.circles.core.model.LoadingData
import org.futo.circles.core.model.ResLoadingData


class LoadingDialog(context: Context) : AppCompatDialog(context) {

    private val binding = DialogLoadingBinding.inflate(LayoutInflater.from(context))

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    fun handleLoading(data: LoadingData) {
        if (data.isLoading) {
            binding.vLoading.setProgress(data)
            if (isShowing.not()) show()
        } else {
            dismiss()
        }
    }
}