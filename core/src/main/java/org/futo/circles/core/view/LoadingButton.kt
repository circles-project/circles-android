package org.futo.circles.core.view

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import org.futo.circles.core.R
import org.futo.circles.core.databinding.ViewLoadingButtonBinding
import org.futo.circles.core.extensions.getAttributes
import org.futo.circles.core.extensions.setIsVisible


class LoadingButtonState(
    val superSavedState: Parcelable?,
    val isLoading: Boolean,
    val text: String,
    val isEnabled: Boolean
) : View.BaseSavedState(superSavedState), Parcelable


class LoadingButton(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewLoadingButtonBinding.inflate(LayoutInflater.from(context), this)

    private var buttonText: String = ""
    var isLoading: Boolean = false
        private set

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

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        return LoadingButtonState(superState, isLoading, buttonText, isEnabled)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val loadingButtonState = state as? LoadingButtonState
        super.onRestoreInstanceState(loadingButtonState?.superSavedState ?: state)
        post {
            setText(loadingButtonState?.text ?: "")
            setIsLoading(loadingButtonState?.isLoading ?: false)
            isEnabled = loadingButtonState?.isEnabled ?: true
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        binding.button.setOnClickListener(l)
    }

    fun setText(text: String) {
        binding.button.text = text
        buttonText = text
    }

    fun setIsLoading(isLoading: Boolean) {
        this.isLoading = isLoading
        binding.loader.setIsVisible(isLoading)
        binding.button.isEnabled = !isLoading
        binding.button.text = if (isLoading) "" else buttonText
    }

    override fun setEnabled(isEnabled: Boolean) {
        super.setEnabled(isEnabled)
        binding.button.isEnabled = isEnabled
    }

}