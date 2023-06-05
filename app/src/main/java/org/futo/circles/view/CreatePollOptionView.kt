package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import org.futo.circles.R
import org.futo.circles.core.extensions.getText
import org.futo.circles.databinding.ViewCreatePollOptionBinding

class CreatePollOptionView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewCreatePollOptionBinding.inflate(LayoutInflater.from(context), this)

    fun setup(
        position: Int,
        onRemove: (CreatePollOptionView) -> Unit,
        textChanged: (String) -> Unit
    ) {
        setHint(position)
        binding.ivRemove.setOnClickListener { onRemove.invoke(this) }
        binding.tilOption.editText?.apply {
            imeOptions = EditorInfo.IME_ACTION_DONE
            doAfterTextChanged {
                textChanged(binding.tilOption.getText())
            }
        }
    }

    fun setImeActionNext() {
        binding.tilOption.editText?.imeOptions = EditorInfo.IME_ACTION_NEXT
    }

    fun setText(text: String) {
        binding.tilOption.editText?.setText(text)
    }

    fun setHint(position: Int) {
        binding.tilOption.hint = context.getString(R.string.option_format, position)
    }

    fun getText() = binding.tilOption.getText()

}