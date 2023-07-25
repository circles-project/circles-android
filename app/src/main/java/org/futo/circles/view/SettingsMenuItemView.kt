package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import org.futo.circles.R
import org.futo.circles.core.extensions.getAttributes
import org.futo.circles.databinding.ViewSettingsMenuItemBinding


class SettingsMenuItemView(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {

    init {
        orientation = VERTICAL
    }

    private val binding =
        ViewSettingsMenuItemBinding.inflate(LayoutInflater.from(context), this)


    init {
        getAttributes(attrs, R.styleable.SettingsMenuItemView) {
            binding.tvOptionName.apply {
                text = getString(R.styleable.SettingsMenuItemView_optionName)
                    ?.replace(' ', Typography.nbsp)
                setCompoundDrawablesWithIntrinsicBounds(
                    getDrawable(R.styleable.SettingsMenuItemView_optionIcon), null, null, null
                )
            }

        }
    }

    fun setText(text: String) {
        binding.tvOptionName.text = text
    }
}