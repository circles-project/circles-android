package com.futo.circles.feature.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.futo.circles.databinding.AdvancedOptionsViewBinding
import com.futo.circles.extensions.setVisibility

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
        val isOpened = binding.btnAdvanced.isOpened()
        binding.tilPassword.setVisibility(!isOpened)
        binding.btnAdvanced.setIsOpened(!isOpened)
    }

}