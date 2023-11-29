package org.futo.circles.core.feature.markdown.span

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.style.DynamicDrawableSpan
import androidx.core.content.ContextCompat
import com.google.android.material.chip.ChipDrawable
import org.futo.circles.core.R

class MentionSpan(
    private val context: Context,
    val name: String
) : DynamicDrawableSpan() {

    override fun getDrawable(): Drawable =
        ChipDrawable.createFromResource(context, R.xml.bg_chip).apply {
            setTextColor(ContextCompat.getColor(context, R.color.blue))
            text = name
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        }
}