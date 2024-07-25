package org.futo.circles.feature.direct.timeline.list.holder

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.android.material.imageview.ShapeableImageView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.Post
import org.futo.circles.databinding.ListItemMyImageDmBinding
import org.futo.circles.databinding.ListItemOtherImageDmBinding
import org.futo.circles.feature.direct.timeline.listeners.DmOptionsListener
import org.futo.circles.feature.timeline.list.holder.ImageMediaViewHolder
import org.futo.circles.view.DmFooterView

abstract class DmImageMessageViewHolder(
    view: View,
    dmOptionsListener: DmOptionsListener
) : DmViewHolder(view, dmOptionsListener), ImageMediaViewHolder {

    abstract val ivMediaContent: ImageView?

    override fun bindHolderSpecific(post: Post) {
        bindImage(post)
    }

    protected fun initListeners() {
        setListeners()
        ivMediaContent?.apply {
            setOnClickListener {
                post?.let { dmOptionsListener.onShowPreview(it.id) }
            }
            setOnLongClickListener {
                post?.let { dmOptionsListener.onShowMenuClicked(it.id) }
                true
            }
        }
    }

    private fun bindImage(post: Post) {
        val content = post.content as? MediaContent ?: return
        ivMediaContent?.let { bindMediaCover(content, it) }
    }
}


class DmMyImageMessageViewHolder(
    parent: ViewGroup,
    dmOptionsListener: DmOptionsListener
) : DmImageMessageViewHolder(
    inflate(parent, ListItemMyImageDmBinding::inflate), dmOptionsListener
) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemMyImageDmBinding

    override val ivMediaContent: ImageView
        get() = binding.ivMediaContent
    override val dmBackground: ShapeableImageView
        get() = binding.ivBackground
    override val dmFooter: DmFooterView
        get() = binding.dmFooter

    init {
        initListeners()
    }
}


class DmOtherImageMessageViewHolder(
    parent: ViewGroup,
    dmOptionsListener: DmOptionsListener
) : DmImageMessageViewHolder(
    inflate(parent, ListItemOtherImageDmBinding::inflate), dmOptionsListener
) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemOtherImageDmBinding

    override val ivMediaContent: ImageView
        get() = binding.ivMediaContent
    override val dmBackground: ShapeableImageView
        get() = binding.ivBackground
    override val dmFooter: DmFooterView
        get() = binding.dmFooter

    init {
        initListeners()
    }

}