package org.futo.circles.feature.direct.timeline.list.holder

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import org.futo.circles.core.base.list.context
import org.futo.circles.core.extensions.dpToPx
import org.futo.circles.core.model.DmTimelineItemPayload
import org.futo.circles.core.model.DmTimelineListItem
import org.futo.circles.core.model.DmTimelineMessage
import org.futo.circles.feature.direct.timeline.listeners.DmOptionsListener
import org.futo.circles.model.DmShapeType
import org.futo.circles.model.DmShapeType.First
import org.futo.circles.model.DmShapeType.Last
import org.futo.circles.model.DmShapeType.Middle
import org.futo.circles.model.DmShapeType.Single
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
        (item as? DmTimelineMessage)?.let {
            val dmShapeType = getDmShapeType(
                item,
                (previousItem as? DmTimelineMessage),
                (nextItem as? DmTimelineMessage)
            )
            bindDmMessage(item, dmShapeType)
        }
    }

    protected fun setListeners() {
        dmBackground.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }
        dmFooter.setListener(dmOptionsListener)
    }


    private fun bindDmMessage(dmMessage: DmTimelineMessage, dmShapeType: DmShapeType) {
        this.dmMessage = dmMessage
        bindMessageLayout(dmMessage, dmShapeType)
        dmFooter.setData(dmMessage)
        bindHolderSpecific(dmMessage)
    }

    fun bindPayload(payload: DmTimelineItemPayload) {
        dmFooter.bindPayload(payload.reactions)
    }

    private fun bindMessageLayout(dmMessage: DmTimelineMessage, dmShapeType: DmShapeType) {
        val smallHorizontalMargin = context.dpToPx(8)
        val bigHorizontalMargin = context.dpToPx(36)
        val topMargin = if (dmShapeType == First || dmShapeType == Single) context.dpToPx(12)
        else context.dpToPx(2)

        val layoutParams = FrameLayout.LayoutParams(
            if (dmMessage.content.isMedia()) FrameLayout.LayoutParams.MATCH_PARENT
            else FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )

        if (dmMessage.isMyMessage()) {
            rootMessageLayout.layoutParams = layoutParams.apply {
                gravity = Gravity.END
                setMargins(bigHorizontalMargin, topMargin, smallHorizontalMargin, 0)
            }
            dmBackground.apply {
                setBackgroundResource(org.futo.circles.core.R.color.primary)
                setShapeAppearanceModel(getMessageBackgroundShapeModel(true, dmShapeType))
            }
        } else {
            rootMessageLayout.layoutParams = layoutParams.apply {
                gravity = Gravity.START
                setMargins(smallHorizontalMargin, topMargin, bigHorizontalMargin, 0)
            }
            dmBackground.apply {
                setBackgroundResource(org.futo.circles.core.R.color.white)
                setShapeAppearanceModel(getMessageBackgroundShapeModel(false, dmShapeType))
            }
        }
    }

    private fun ShapeableImageView.getMessageBackgroundShapeModel(
        isMyMessage: Boolean,
        dmShapeType: DmShapeType
    ): ShapeAppearanceModel {
        val bigRadius = context.dpToPx(16).toFloat()
        val smallRadius = context.dpToPx(2).toFloat()
        val cornerFamily = CornerFamily.ROUNDED
        val builder = shapeAppearanceModel.toBuilder()
            .setTopLeftCorner(cornerFamily, bigRadius)
            .setTopRightCorner(cornerFamily, bigRadius)
            .setBottomLeftCorner(cornerFamily, bigRadius)
            .setBottomRightCorner(cornerFamily, bigRadius)

        when (dmShapeType) {
            First -> if (isMyMessage) builder.setBottomRightCorner(cornerFamily, smallRadius)
            else builder.setBottomLeftCorner(cornerFamily, smallRadius)

            Last -> if (isMyMessage) builder.setTopRightCorner(cornerFamily, smallRadius)
            else builder.setTopLeftCorner(cornerFamily, smallRadius)

            Middle -> if (isMyMessage) {
                builder.setTopRightCorner(cornerFamily, smallRadius)
                builder.setBottomRightCorner(cornerFamily, smallRadius)
            } else {
                builder.setTopLeftCorner(cornerFamily, smallRadius)
                builder.setBottomLeftCorner(cornerFamily, smallRadius)
            }

            Single -> {}
        }

        return builder.build()
    }

    private fun getDmShapeType(
        item: DmTimelineMessage, previousItem: DmTimelineMessage?,
        nextItem: DmTimelineMessage?
    ): DmShapeType {
        val currentItemSenderId = item.info.sender.userId
        val previousSenderId = previousItem?.info?.sender?.userId
        val nextSenderId = nextItem?.info?.sender?.userId

        return if (previousSenderId == currentItemSenderId && nextSenderId == currentItemSenderId) Middle
        else if (previousSenderId == currentItemSenderId) Last
        else if (nextSenderId == currentItemSenderId) First
        else Single
    }

}


