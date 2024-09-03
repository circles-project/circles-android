package org.futo.circles.feature.direct.timeline.list.holder

import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.model.DmTimelineMessage
import org.futo.circles.core.model.TextContent
import org.futo.circles.databinding.ListItemMyTextDmBinding
import org.futo.circles.extensions.handleLinkClick
import org.futo.circles.feature.direct.timeline.listeners.DmOptionsListener
import org.futo.circles.view.DmFooterView

class DmTextMessageViewHolder(
    parent: ViewGroup,
    dmOptionsListener: DmOptionsListener
) : DmViewHolder(inflate(parent, ListItemMyTextDmBinding::inflate), dmOptionsListener) {


    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemMyTextDmBinding

    override val dmBackground: ShapeableImageView
        get() = binding.ivBackground
    override val dmFooter: DmFooterView
        get() = binding.dmFooter
    private val tvMessage: TextView
        get() = binding.tvMessage

    init {
        setListeners()
        tvMessage.apply {
            handleLinkClick()
            setOnLongClickListener {
                dmMessage?.let { dmOptionsListener.onShowMenuClicked(it.id) }
                true
            }
        }
    }

    override fun bindHolderSpecific(dmMessage: DmTimelineMessage) {
        bindTextMessage(dmMessage)
    }

    private fun bindTextMessage(dmMessage: DmTimelineMessage) {
        val content = dmMessage.content as? TextContent ?: return
        tvMessage.setText(content.messageSpanned, TextView.BufferType.SPANNABLE)
    }
}