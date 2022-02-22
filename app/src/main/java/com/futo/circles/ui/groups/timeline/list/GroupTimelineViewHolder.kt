package com.futo.circles.ui.groups.timeline.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.base.ViewBindingHolder
import com.futo.circles.databinding.GroupImageMessageListItemBinding
import com.futo.circles.databinding.GroupTextMessageListItemBinding
import com.futo.circles.extensions.loadMatrixThumbnail
import com.futo.circles.ui.groups.timeline.model.GroupImageMessage
import com.futo.circles.ui.groups.timeline.model.GroupTextMessage
import org.matrix.android.sdk.api.session.content.ContentUrlResolver

sealed class GroupTimelineViewHolder(view: View) : RecyclerView.ViewHolder(view)

class TextMessageViewHolder(parent: ViewGroup) :
    GroupTimelineViewHolder(
        inflate(parent, GroupTextMessageListItemBinding::inflate)
    ) {

    private companion object : ViewBindingHolder<GroupTextMessageListItemBinding>

    fun bind(data: GroupTextMessage) {
        binding.tvMessage.text = data.message
    }

}

class ImageMessageViewHolder(parent: ViewGroup, private val urlResolver: ContentUrlResolver?) :
    GroupTimelineViewHolder(
        inflate(parent, GroupImageMessageListItemBinding::inflate)
    ) {

    private companion object : ViewBindingHolder<GroupImageMessageListItemBinding>

    fun bind(data: GroupImageMessage) {
        binding.tvImage.loadMatrixThumbnail(data.encryptedImageUrl, urlResolver)
    }

}