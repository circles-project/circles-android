package org.futo.circles.core.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import org.futo.circles.core.R
import org.futo.circles.core.databinding.ViewNotificationCounterBinding
import org.futo.circles.core.extensions.getAttributes
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.feature.textDrawable.TextDrawable

class NotificationCounterView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewNotificationCounterBinding.inflate(LayoutInflater.from(context), this)


    private var notificationCircleColor: Int = -1

    init {
        getAttributes(attrs, R.styleable.NotificationCounterView) {
            notificationCircleColor = getColor(
                R.styleable.NotificationCounterView_notificationCircleBackground,
                ContextCompat.getColor(context, android.R.color.holo_red_dark)
            )
        }
    }

    fun setCount(count: Int) {
        setIsVisible(count > 0)
        if (count > 0) {
            binding.ivCounter.setImageDrawable(
                TextDrawable.Builder()
                    .setShape(TextDrawable.SHAPE_ROUND_RECT)
                    .setColor(notificationCircleColor)
                    .setTextColor(Color.WHITE)
                    .setBold()
                    .setText(count.toString())
                    .build()
            )
        }
    }
}