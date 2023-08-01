package org.futo.circles.core.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.futo.circles.core.databinding.ViewEmptyTabPlaceholderBinding
import org.futo.circles.core.extensions.setIsVisible

class EmptyTabPlaceholderView(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {


    private val binding =
        ViewEmptyTabPlaceholderBinding.inflate(LayoutInflater.from(context), this)


    fun setText(message: String) {
        binding.tvEmptyMessage.text = message
    }

    fun setArrowVisible(isVisible: Boolean) {
        binding.ivArrow.setIsVisible(isVisible)
    }

}
