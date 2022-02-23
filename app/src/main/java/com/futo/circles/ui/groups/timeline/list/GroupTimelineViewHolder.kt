package com.futo.circles.ui.groups.timeline.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.base.ViewBindingHolder
import com.futo.circles.databinding.GroupImageMessageListItemBinding
import com.futo.circles.databinding.GroupTextMessageListItemBinding
import com.futo.circles.extensions.loadMatrixThumbnail
import com.futo.circles.extensions.setIsEncryptedIcon
import com.futo.circles.ui.groups.timeline.model.GroupImageMessage
import com.futo.circles.ui.groups.timeline.model.GroupTextMessage
import org.matrix.android.sdk.api.session.content.ContentUrlResolver
import java.text.DateFormat
import java.util.*

sealed class GroupTimelineViewHolder(view: View) : RecyclerView.ViewHolder(view)

class TextMessageViewHolder(parent: ViewGroup, private val urlResolver: ContentUrlResolver?) :
    GroupTimelineViewHolder(
        inflate(parent, GroupTextMessageListItemBinding::inflate)
    ) {

    private companion object : ViewBindingHolder<GroupTextMessageListItemBinding>

    fun bind(data: GroupTextMessage) {
        binding.ivSenderImage.loadMatrixThumbnail(
            data.sender.avatarUrl,
            urlResolver,
            binding.ivSenderImage.height
        )
        binding.tvUserName.text = data.sender.disambiguatedDisplayName
        binding.tvUserId.text = data.sender.userId
        binding.tvMessage.text = data.message
        binding.ivEncrypted.setIsEncryptedIcon(data.isEncrypted)
        binding.tvMessageTime.text = DateFormat.getDateTimeInstance().format(Date(data.timestamp))
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