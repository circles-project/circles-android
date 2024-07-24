package org.futo.circles.feature.direct.timeline.list.holder

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.TextContent
import org.futo.circles.databinding.ListItemMyTextDmBinding
import org.futo.circles.databinding.ListItemOtherTextDmBinding
import org.futo.circles.feature.direct.timeline.listeners.DmOptionsListener
import org.futo.circles.view.DmFooterView

abstract class DmTextMessageViewHolder(
    view: View,
    dmOptionsListener: DmOptionsListener
) : DmViewHolder(view, dmOptionsListener) {

    abstract val tvMessage: TextView?

    protected fun initListeners() {
        setListeners()
        tvMessage?.apply {
            handleLinkClick(this)
            setOnLongClickListener {
                post?.let { dmOptionsListener.onShowMenuClicked(it.id) }
                true
            }
        }
    }

    override fun bindHolderSpecific(post: Post) {
        bindTextMessage(post)
    }

    private fun bindTextMessage(post: Post) {
        val content = post.content as? TextContent ?: return
        tvMessage?.setText(content.messageSpanned, TextView.BufferType.SPANNABLE)
    }
}

class DmMyTextMessageViewHolder(
    parent: ViewGroup,
    dmOptionsListener: DmOptionsListener
) : DmTextMessageViewHolder(
    inflate(parent, ListItemMyTextDmBinding::inflate), dmOptionsListener
) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemMyTextDmBinding

    override val dmBackground: ShapeableImageView
        get() = binding.ivBackground
    override val dmFooter: DmFooterView
        get() = binding.dmFooter
    override val tvMessage: TextView
        get() = binding.tvMessage

    init {
        initListeners()
    }

}

class DmOtherTextMessageViewHolder(
    parent: ViewGroup,
    dmOptionsListener: DmOptionsListener
) : DmTextMessageViewHolder(
    inflate(parent, ListItemOtherTextDmBinding::inflate), dmOptionsListener
) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemOtherTextDmBinding

    override val dmBackground: ShapeableImageView
        get() = binding.ivBackground
    override val dmFooter: DmFooterView
        get() = binding.dmFooter
    override val tvMessage: TextView
        get() = binding.tvMessage

    init {
        initListeners()
    }

}