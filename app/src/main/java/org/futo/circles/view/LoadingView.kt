package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import org.futo.circles.model.LoadingData
import org.futo.circles.databinding.LoadingViewBinding
import org.futo.circles.extensions.setIsVisible

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