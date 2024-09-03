package org.futo.circles.feature.direct.timeline.list.holder

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.google.android.material.imageview.ShapeableImageView
import org.futo.circles.core.base.list.context
import org.futo.circles.core.model.DmTimelineItemPayload
import org.futo.circles.core.model.DmTimelineListItem
import org.futo.circles.core.model.DmTimelineMessage
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostListItem
import org.futo.circles.feature.direct.timeline.listeners.DmOptionsListener
import org.futo.circles.feature.timeline.list.holder.TimelineListItemViewHolder
import org.futo.circles.model.PostItemPayload
import org.futo.circles.view.DmFooterView


@SuppressLint("ClickableViewAccessibility")
abstract class DmViewHolder(
    view: View,
    protected val dmOptionsListener: DmOptionsListener
) : DmTimelineListItemViewHolder(view) {

    abstract val dmBackground: ShapeableImageView?
    abstract val dmFooter: DmFooterView?
    abstract fun bindHolderSpecific(dmMessage: DmTimelineMessage)

    protected var dmMessage: DmTimelineMessage? = null

    private val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                dmMessage?.let {
                    dmOptionsListener.onShowEmoji(it.id) { emoji ->
                        dmFooter?.addEmojiFromPickerLocalUpdate(emoji)
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

    override fun bind(item: DmTimelineListItem) {
        (item as? DmTimelineMessage)?.let { bindDmMessage(item) }
    }

    protected fun setListeners() {
        dmBackground?.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }
        dmFooter?.setListener(dmOptionsListener)
    }

    private fun bindDmMessage(dmMessage: DmTimelineMessage) {
        this.dmMessage = dmMessage
        dmFooter?.setData(dmMessage)
        bindHolderSpecific(dmMessage)
    }

    fun bindPayload(payload: DmTimelineItemPayload) {
        dmFooter?.bindPayload(payload.reactions)
    }

}


