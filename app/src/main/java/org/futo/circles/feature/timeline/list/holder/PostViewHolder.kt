package org.futo.circles.feature.timeline.list.holder

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.futo.circles.R
import org.futo.circles.core.base.list.context
import org.futo.circles.core.feature.markdown.MarkdownParser
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.PollContent
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostContent
import org.futo.circles.core.model.PostListItem
import org.futo.circles.core.model.TextContent
import org.futo.circles.feature.timeline.InternalLinkMovementMethod
import org.futo.circles.feature.timeline.list.OnLinkClickedListener
import org.futo.circles.feature.timeline.list.PostOptionsListener
import org.futo.circles.model.PostItemPayload
import org.futo.circles.view.PostFooterView
import org.futo.circles.view.PostHeaderView
import org.futo.circles.view.PostStatusView
import org.futo.circles.view.ReadMoreTextView
import org.matrix.android.sdk.api.extensions.tryOrNull


@SuppressLint("ClickableViewAccessibility")
abstract class PostViewHolder(
    view: View,
    protected val optionsListener: PostOptionsListener,
    private val isThread: Boolean
) : PostListItemViewHolder(view) {

    abstract val postLayout: ViewGroup?
    abstract val postFooter: PostFooterView?
    abstract val postStatus: PostStatusView?
    abstract val readMoreTextView: ReadMoreTextView?
    abstract val postHeader: PostHeaderView
    abstract fun bindHolderSpecific(post: Post)

    protected var post: Post? = null

    private val gestureDetector =
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                post?.let {
                    optionsListener.onShowEmoji(it.postInfo.roomId, it.id) { emoji ->
                        postFooter?.addEmojiFromPickerLocalUpdate(emoji)
                    }
                }
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
        handleLinkClick()
        readMoreTextView?.apply {
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
        bindMentionBorder(post.content)
        postStatus?.apply {
            setIsEdited(post.postInfo.isEdited)
            setSendStatus(post.sendState, post.readByCount)
        }
        bindHolderSpecific(post)
    }

    fun bindPayload(payload: PostItemPayload) {
        postStatus?.setSendStatus(payload.sendState, payload.readByCount)
        postFooter?.bindPayload(payload.repliesCount, payload.reactions)
    }

    private fun bindMentionBorder(content: PostContent) {
        val hasMention = when (content) {
            is MediaContent -> content.caption?.let {
                MarkdownParser.hasCurrentUserMention(it)
            } ?: false

            is TextContent -> MarkdownParser.hasCurrentUserMention(content.message)
            is PollContent -> MarkdownParser.hasCurrentUserMention(content.question)
            else -> false
        }
        if (hasMention) postLayout?.setBackgroundResource(R.drawable.bg_mention_highlight)
        else postLayout?.background = null
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun handleLinkClick() {
        readMoreTextView?.apply {
            movementMethod = InternalLinkMovementMethod(object : OnLinkClickedListener {
                override fun onLinkClicked(url: String) {
                    showLinkConfirmation(context, url)
                }
            })
            setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) v.requestFocus()
                false
            }
        }
    }

    private fun showLinkConfirmation(context: Context, url: String) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.do_you_want_to_open_this_url)
            .setMessage(url)
            .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                tryOrNull {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
                dialogInterface.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun openReplies() {
        post?.let { optionsListener.onReply(it.postInfo.roomId, it.id) }
    }

}


