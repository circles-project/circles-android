package org.futo.circles.core.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.futo.circles.core.R
import org.futo.circles.core.databinding.ViewSettingsMenuItemBinding
import org.futo.circles.core.extensions.getAttributes
import org.futo.circles.core.extensions.setIsVisible


class SettingsMenuItemView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewSettingsMenuItemBinding.inflate(LayoutInflater.from(context), this)


    init {
        addBackgroundRipple()
        getAttributes(attrs, R.styleable.SettingsMenuItemView) {
            val isDividerVisible = getBoolean(R.styleable.SettingsMenuItemView_hasDivider, true)
            binding.vBottomDivider.setIsVisible(isDividerVisible)
            binding.tvOptionName.text = getString(R.styleable.SettingsMenuItemView_optionName)
                ?.replace(' ', Typography.nbsp)
            binding.ivOptionIcon.setImageDrawable(getDrawable(R.styleable.SettingsMenuItemView_optionIcon))
        }
    }

    fun setText(text: String) {
        binding.tvOptionName.text = text
    }

    private fun addBackgroundRipple() {
        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
        setBackgroundResource(outValue.resourceId)
    }
}