package org.futo.circles.feature.direct.timeline.list.holder

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.google.android.material.imageview.ShapeableImageView
import org.futo.circles.core.base.list.context
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostListItem
import org.futo.circles.feature.direct.timeline.listeners.DmOptionsListener
import org.futo.circles.feature.timeline.base.TimelineListItemViewHolder
import org.futo.circles.model.PostItemPayload
import org.futo.circles.view.DmFooterView


@SuppressLint("ClickableViewAccessibility")
abstract class DmViewHolder(
    view: View,
    protected val dmOptionsListener: DmOptionsListener
) : TimelineListItemViewHolder(view) {

    abstract val dmBackground: ShapeableImageView?
    abstract val dmFooter: DmFooterView?
    abstract fun bindHolderSpecific(post: Post)

    protected var post: Post? = null

    private val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                post?.let {
                    dmOptionsListener.onShowEmoji(it.id) { emoji ->
                        dmFooter?.addEmojiFromPickerLocalUpdate(emoji)
                    }
                }
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                post?.let {
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

    override fun bind(item: PostListItem) {
        (item as? Post)?.let { bindPost(item) }
    }

    protected fun setListeners() {
        dmBackground?.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }
        dmFooter?.setListener(dmOptionsListener)
    }

    private fun bindPost(post: Post) {
        this.post = post
        dmFooter?.setData(post)
        bindHolderSpecific(post)
    }

    fun bindPayload(payload: PostItemPayload) {
        dmFooter?.bindPayload(payload.reactions)
    }

}


