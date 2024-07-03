package org.futo.circles.settings.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.futo.circles.settings.databinding.ViewEditEmailBinding


class EditEmailView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewEditEmailBinding.inflate(LayoutInflater.from(context), this)

    fun setData(
        email: String,
        onRemove: (String) -> Unit,
    ) {
        with(binding) {
            tvEmail.text = email
            binding.ivRemove.setOnClickListener { onRemove.invoke(email) }
        }
    }
}