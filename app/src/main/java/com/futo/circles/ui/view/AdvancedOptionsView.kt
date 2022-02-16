package com.futo.circles.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.futo.circles.R
import com.futo.circles.databinding.AdvancedOptionsViewBinding
import com.futo.circles.extensions.gone
import com.futo.circles.extensions.visible

class AdvancedOptionsView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        AdvancedOptionsViewBinding.inflate(LayoutInflater.from(context), this)

    init {
        binding.btnAdvanced.setOnClickListener {
            toggleEncryptionPasswordVisibility()
        }
    }

    fun getText(): String? =
        binding.tilPassword.editText?.text.toString().takeIf { it.isNotEmpty() }

    private fun toggleEncryptionPasswordVisibility() {
        if (binding.tilPassword.isVisible) {
            binding.tilPassword.gone()
            binding.btnAdvanced.apply {
                text = context.getString(R.string.advanced_options)
                setIconResource(R.drawable.ic_keyboard_arrow_right)
            }
        } else {
            binding.tilPassword.visible()
            binding.btnAdvanced.apply {
                text = context.getString(R.string.hide_advanced_options)
                setIconResource(R.drawable.ic_keyboard_arrow_down)
            }
        }
    }

}