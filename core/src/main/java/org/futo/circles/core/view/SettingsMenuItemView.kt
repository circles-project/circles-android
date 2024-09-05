package org.futo.circles.core.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import org.futo.circles.core.R
import org.futo.circles.core.databinding.ViewSettingsMenuItemBinding
import org.futo.circles.core.extensions.getAttributes
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.visible


class SettingsMenuItemView(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    private val binding =
        ViewSettingsMenuItemBinding.inflate(LayoutInflater.from(context), this)


    init {
        getAttributes(attrs, R.styleable.SettingsMenuItemView) {
            val isDividerVisible = getBoolean(R.styleable.SettingsMenuItemView_hasDivider, true)
            binding.vBottomDivider.setIsVisible(isDividerVisible)
            binding.tvOptionName.text = getString(R.styleable.SettingsMenuItemView_optionName)
                ?.replace(' ', Typography.nbsp)

            getDrawable(R.styleable.SettingsMenuItemView_optionIcon)?.let {
                binding.ivOptionIcon.apply {
                    visible()
                    setImageDrawable(it)
                }
            } ?: binding.ivOptionIcon.gone()

        }
    }

    fun setText(text: String) {
        binding.tvOptionName.text = text
    }
}