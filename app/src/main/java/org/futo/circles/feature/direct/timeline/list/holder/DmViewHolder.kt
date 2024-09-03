package org.futo.circles.feature.direct.timeline.list.holder

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import org.futo.circles.core.base.list.context
import org.futo.circles.core.extensions.dpToPx
import org.futo.circles.core.model.DmTimelineItemPayload
import org.futo.circles.core.model.DmTimelineListItem
import org.futo.circles.core.model.DmTimelineMessage
import org.futo.circles.feature.direct.timeline.listeners.DmOptionsListener
import org.futo.circles.view.DmFooterView


@SuppressLint("ClickableViewAccessibility")
abstract class DmViewHolder(
    view: View,
    protected val dmOptionsListener: DmOptionsListener
) : DmTimelineListItemViewHolder(view) {

    abstract val rootMessageLayout: FrameLayout
    abstract val dmBackground: ShapeableImageView
    abstract val dmFooter: DmFooterView
    abstract fun bindHolderSpecific(dmMessage: DmTimelineMessage)

    protected var dmMessage: DmTimelineMessage? = null

    private val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                dmMessage?.let {
                    dmOptionsListener.onShowEmoji(it.id) { emoji ->
                        dmFooter.addEmojiFromPickerLocalUpdate(emoji)
                    }
                }
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                dmMessage?.let {
                    dmOptionsListener.onShowMenuClicked(it.id)
                }
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                return true
            }

            override fun onDoubleTapEvent(e: MotionEvent) = true
            override fun onDown(e: MotionEvent) = true
        }).apply {
            setIsLongpressEnabled(true)
        }

    override fun bind(
        item: DmTimelineListItem, previousItem: DmTimelineListItem?,
        nextItem: DmTimelineListItem?
    ) {
        (item as? DmTimelineMessage)?.let { bindDmMessage(item) }
    }

    protected fun setListeners() {
        dmBackground.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }
        dmFooter.setListener(dmOptionsListener)
    }

    private fun bindDmMessage(dmMessage: DmTimelineMessage) {
        this.dmMessage = dmMessage

        if (dmMessage.isMyMessage()) {
            rootMessageLayout.updateLayoutParams {
                (rootMessageLayout.layoutParams as? FrameLayout.LayoutParams)?.apply {
                    gravity = Gravity.END
                    marginStart = context.dpToPx(36)
                    marginEnd = context.dpToPx(8)
                }
            }
            dmBackground.apply {
                setBackgroundResource(org.futo.circles.core.R.color.primary)
                val radius = context.dpToPx(10).toFloat()
                setShapeAppearanceModel(
                    shapeAppearanceModel
                        .toBuilder()
                        .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                        .setTopRightCorner(CornerFamily.ROUNDED, radius)
                        .setBottomLeftCorner(CornerFamily.ROUNDED, radius)
                        .setBottomRightCorner(CornerFamily.ROUNDED, 0.0f)
                        .build()
                )
            }

        } else {
            rootMessageLayout.updateLayoutParams {
                (rootMessageLayout.layoutParams as? FrameLayout.LayoutParams)?.apply {
                    gravity = Gravity.START
                    marginStart = context.dpToPx(8)
                    marginEnd = context.dpToPx(36)
                }
            }
            dmBackground.apply {
                setBackgroundResource(org.futo.circles.core.R.color.white)
                val radius = context.dpToPx(10).toFloat()
                setShapeAppearanceModel(
                    shapeAppearanceModel
                        .toBuilder()
                        .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                        .setTopRightCorner(CornerFamily.ROUNDED, radius)
                        .setBottomLeftCorner(CornerFamily.ROUNDED, 0.0f)
                        .setBottomRightCorner(CornerFamily.ROUNDED, radius)
                        .build()
                )
            }
        }
        dmFooter.setData(dmMessage)
        bindHolderSpecific(dmMessage)
    }

    fun bindPayload(payload: DmTimelineItemPayload) {
        dmFooter.bindPayload(payload.reactions)
    }

}


