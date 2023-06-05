package org.futo.circles.auth.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatAutoCompleteTextView

@SuppressLint("ClickableViewAccessibility")
class DomainAutocompleteView(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatAutoCompleteTextView(context, attrs) {

    init {
        setOnTouchListener { _, _ ->
            postDelayed( { if (hasFocus()) showDropDown() },100)
            return@setOnTouchListener false
        }
    }

    override fun enoughToFilter(): Boolean {
        return true
    }

    override fun performFiltering(text: CharSequence?, keyCode: Int) {
        super.performFiltering("", keyCode)
    }
}