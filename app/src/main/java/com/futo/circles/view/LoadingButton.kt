package com.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.futo.circles.R
import com.futo.circles.databinding.LoadingButtonViewBinding
import com.futo.circles.extensions.getAttributes
import com.futo.circles.extensions.setIsVisible

class LoadingButton(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding =
        LoadingButtonViewBinding.inflate(LayoutInflater.from(context), this)

    private var buttonText: String = ""

    init {
        getAttributes(attrs, R.styleable.LoadingButton) {
            getText(R.styleable.LoadingButton_android_text)?.let {
                buttonText = it.toString()
                binding.button.text = it
            }
            getDimensionPixelSize(R.styleable.LoadingButton_android_textSize, 0).takeIf { it > 0 }
                ?.let {
                    binding.button.setTextSize(TypedValue.COMPLEX_UNIT_PX, it.toFloat())
                }
            getDimensionPixelSize(R.styleable.LoadingButton_textPadding, 0).takeIf { it > 0 }
                ?.let {
                    binding.button.setPadding(it, it, it, it)
                }
            binding.button.isEnabled =
                getBoolean(R.styleable.LoadingButton_android_enabled, true)
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        binding.button.setOnClickListener(l)
    }


    fun setIsLoading(isLoading: Boolean) {
        binding.loader.setIsVisible(isLoading)
        binding.button.isEnabled = !isLoading
        binding.button.text = if (isLoading) "" else buttonText
    }

    override fun setEnabled(isEnabled: Boolean) {
        super.setEnabled(isEnabled)
        binding.button.isEnabled = isEnabled
    }

}