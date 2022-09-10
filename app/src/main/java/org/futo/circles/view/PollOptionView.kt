package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import org.futo.circles.R
import org.futo.circles.databinding.ViewPollOptionBinding
import org.futo.circles.extensions.getText

class PollOptionView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewPollOptionBinding.inflate(LayoutInflater.from(context), this)

    fun setup(position: Int, onRemove: (PollOptionView) -> Unit, textChanged: (String) -> Unit) {
        setHint(position)
        binding.ivRemove.setOnClickListener { onRemove.invoke(this) }
        binding.tilOption.editText?.doAfterTextChanged {
            textChanged(binding.tilOption.getText())
        }
    }

    fun setHint(position: Int) {
        binding.tilOption.hint = context.getString(R.string.option_format, position)
    }

    fun getText() = binding.tilOption.getText()

}