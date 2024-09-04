package org.futo.circles.feature.direct.timeline.list.holder

import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.imageview.ShapeableImageView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.model.DmTimelineMessage
import org.futo.circles.core.model.TextContent
import org.futo.circles.databinding.ListItemTextDmBinding
import org.futo.circles.extensions.handleLinkClick
import org.futo.circles.feature.direct.timeline.listeners.DmOptionsListener
import org.futo.circles.view.DmFooterView

class DmTextMessageViewHolder(
    parent: ViewGroup,
    dmOptionsListener: DmOptionsListener
) : DmViewHolder(inflate(parent, ListItemTextDmBinding::inflate), dmOptionsListener) {


    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemTextDmBinding

    override val rootMessageLayout: FrameLayout
        get() = binding.rootMessageLayout
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
        val content = dmMessage.content as? TextContent ?: return
        val textColor = ContextCompat.getColor(
            context,
            if (dmMessage.isMyMessage()) org.futo.circles.core.R.color.white else org.futo.circles.core.R.color.grey_cool_1100
        )
        val textColorLink = ContextCompat.getColor(
            context,
            if (dmMessage.isMyMessage()) org.futo.circles.core.R.color.white else org.futo.circles.core.R.color.blue
        )
        tvMessage.apply {
            setLinkTextColor(textColorLink)
            setTextColor(textColor)
            setText(content.messageSpanned, TextView.BufferType.SPANNABLE)
        }
    }
}