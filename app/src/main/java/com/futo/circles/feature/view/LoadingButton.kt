package com.futo.circles.feature.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.futo.circles.R
import com.futo.circles.databinding.LoadingButtonViewBinding
import com.futo.circles.extensions.getAttributes
import com.futo.circles.extensions.setVisibility

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
        }
    }

    fun setOnClickWithLoading(onClick: () -> Unit) {
        binding.button.setOnClickListener {
            setIsLoading(true)
            onClick()
        }
    }

    fun setIsLoading(isLoading: Boolean) {
        binding.loader.setVisibility(isLoading)
        binding.button.isEnabled = !isLoading
        binding.button.text = if (isLoading) "" else buttonText
    }

}