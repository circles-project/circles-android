package com.futo.circles.ui.groups.timeline.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.base.ViewBindingHolder
import com.futo.circles.databinding.GroupImageMessageListItemBinding
import com.futo.circles.databinding.GroupTextMessageListItemBinding
import com.futo.circles.extensions.loadMatrixThumbnail
import com.futo.circles.ui.groups.timeline.model.GroupImageMessage
import com.futo.circles.ui.groups.timeline.model.GroupMessage
import com.futo.circles.ui.groups.timeline.model.GroupTextMessage
import com.futo.circles.ui.view.GroupPostFooterView
import com.futo.circles.ui.view.GroupPostHeaderView
import org.matrix.android.sdk.api.session.content.ContentUrlResolver

sealed class GroupTimelineViewHolder(view: View, private val urlResolver: ContentUrlResolver?) :
    RecyclerView.ViewHolder(view) {

    abstract val headerView: GroupPostHeaderView
    abstract val footerView: GroupPostFooterView

    protected fun baseBind(data: GroupMessage) {
        headerView.setData(data.sender, urlResolver)
        footerView.setData(data.isEncrypted, data.timestamp)
    }
}

class TextMessageViewHolder(parent: ViewGroup, urlResolver: ContentUrlResolver?) :
    GroupTimelineViewHolder(
        inflate(parent, GroupTextMessageListItemBinding::inflate),
        urlResolver
    ) {

    private companion object : ViewBindingHolder<GroupTextMessageListItemBinding>

    override val headerView: GroupPostHeaderView
        get() = binding.postHeader
    override val footerView: GroupPostFooterView
        get() = binding.postFooter

    fun bind(data: GroupTextMessage) {
        baseBind(data)
        binding.tvMessage.text = data.message
    }

}

class ImageMessageViewHolder(parent: ViewGroup, private val urlResolver: ContentUrlResolver?) :
    GroupTimelineViewHolder(
        inflate(parent, GroupImageMessageListItemBinding::inflate),
        urlResolver
    ) {

    private companion object : ViewBindingHolder<GroupImageMessageListItemBinding>

    override val headerView: GroupPostHeaderView
        get() = binding.postHeader
    override val footerView: GroupPostFooterView
        get() = binding.postFooter

    fun bind(data: GroupImageMessage) {
        baseBind(data)
        binding.ivImage.loadMatrixThumbnail(data.encryptedImageUrl, urlResolver)
    }

}