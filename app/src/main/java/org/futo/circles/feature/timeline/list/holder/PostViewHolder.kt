package org.futo.circles.feature.timeline.list.holder

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.cardview.widget.CardView
import org.futo.circles.core.base.list.context
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostListItem
import org.futo.circles.extensions.handleLinkClick
import org.futo.circles.feature.timeline.list.PostOptionsListener
import org.futo.circles.model.PostItemPayload
import org.futo.circles.view.PostFooterView
import org.futo.circles.view.PostHeaderView
import org.futo.circles.view.ReadMoreTextView


@SuppressLint("ClickableViewAccessibility")
abstract class PostViewHolder(
    view: View,
    protected val optionsListener: PostOptionsListener,
    private val isThread: Boolean
) : TimelineListItemViewHolder(view) {

    abstract val postLayout: CardView?
    abstract val postFooter: PostFooterView?
    abstract val readMoreTextView: ReadMoreTextView?
    abstract val postHeader: PostHeaderView
    abstract fun bindHolderSpecific(post: Post)

    protected var post: Post? = null

    private val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                postFooter?.onLikeIconClicked()
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                postHeader.showMenu()
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                openReplies()
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
        postLayout?.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }
        postFooter?.setListener(optionsListener)
        postHeader.setListener(optionsListener)
        readMoreTextView?.apply {
            handleLinkClick(this)
            setNotCollapsableClickAction { openReplies() }
            setOnLongClickListener {
                postHeader.showMenu()
                true
            }
        }
    }

    private fun bindPost(post: Post) {
        this.post = post
        postHeader.setData(post)
        postFooter?.setData(post, isThread)
        bindHolderSpecific(post)
    }

    fun bindPayload(payload: PostItemPayload) {
        postFooter?.bindPayload(payload.repliesCount, payload.reactions)
    }

    private fun openReplies() {
        post?.let { optionsListener.onReply(it.postInfo.roomId, it.id) }
    }

}


