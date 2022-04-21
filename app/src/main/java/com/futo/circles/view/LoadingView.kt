package com.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.futo.circles.model.LoadingData
import com.futo.circles.databinding.LoadingViewBinding
import com.futo.circles.extensions.setIsVisible

class LoadingView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        LoadingViewBinding.inflate(LayoutInflater.from(context), this)


    fun setMessage(@StringRes messageId: Int) {
        binding.tvLoadingMessage.setText(messageId)
    }

    fun setProgress(data: LoadingData) {
        with(binding) {
            horizontalProgress.max = data.total
            horizontalProgress.progress = data.progress
            horizontalProgress.setIsVisible(data.total != data.progress)
        }
    }
}