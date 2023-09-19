package org.futo.circles.core.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.futo.circles.core.databinding.ViewLoadingBinding
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.model.LoadingData

class LoadingView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewLoadingBinding.inflate(LayoutInflater.from(context), this)


    fun setProgress(data: LoadingData) {
        with(binding) {
            tvLoadingMessage.setText(data.messageId)
            horizontalProgress.apply {
                setIsVisible(data.total != data.progress)
                max = data.total
                progress = data.progress
            }
        }
    }
}