package org.futo.circles.feature.direct.timeline.list.holder

import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.material.imageview.ShapeableImageView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.model.DmTimelineMessage
import org.futo.circles.core.model.MediaContent
import org.futo.circles.databinding.ListItemMyImageDmBinding
import org.futo.circles.feature.direct.timeline.listeners.DmOptionsListener
import org.futo.circles.feature.timeline.list.holder.ImageMediaViewHolder
import org.futo.circles.view.DmFooterView

class DmImageMessageViewHolder(
    parent: ViewGroup,
    dmOptionsListener: DmOptionsListener
) : DmViewHolder(inflate(parent, ListItemMyImageDmBinding::inflate), dmOptionsListener),
    ImageMediaViewHolder {


    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemMyImageDmBinding

    private val ivMediaContent: ImageView
        get() = binding.ivMediaContent
    override val dmBackground: ShapeableImageView
        get() = binding.ivBackground
    override val dmFooter: DmFooterView
        get() = binding.dmFooter

    init {
        initListeners()
    }


    override fun bindHolderSpecific(dmMessage: DmTimelineMessage) {
        bindImage(dmMessage)
    }

    private fun initListeners() {
        setListeners()
        ivMediaContent.apply {
            setOnClickListener {
                dmMessage?.let { dmOptionsListener.onShowPreview(it.id) }
            }
            setOnLongClickListener {
                dmMessage?.let { dmOptionsListener.onShowMenuClicked(it.id) }
                true
            }
        }
    }

    private fun bindImage(dmMessage: DmTimelineMessage) {
        val content = dmMessage.content as? MediaContent ?: return
        bindMediaCover(content, ivMediaContent)
    }
}