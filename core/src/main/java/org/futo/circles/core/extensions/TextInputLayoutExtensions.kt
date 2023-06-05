package org.futo.circles.core.extensions

import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.getText(trim: Boolean = true): String {
    val text = editText?.text?.toString() ?: ""
    return if (trim) text.trim() else text
}