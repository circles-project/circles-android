package com.futo.circles.view

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.futo.circles.R
import com.futo.circles.databinding.GroupPostFooterViewBinding
import com.futo.circles.extensions.getAttributes
import com.futo.circles.extensions.setIsEncryptedIcon
import com.futo.circles.extensions.setIsVisible
import com.futo.circles.feature.share.ImageShareable
import com.futo.circles.feature.share.ShareableContent
import com.futo.circles.feature.share.TextShareable
import com.futo.circles.model.ImageContent
import com.futo.circles.model.Post
import com.futo.circles.model.TextContent
import java.text.DateFormat
import java.util.*


class GroupPostFooterView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        GroupPostFooterViewBinding.inflate(LayoutInflater.from(context), this)

    private var listener: GroupPostListener? = null
    private var post: Post? = null

    init {
        parseAttributes(attrs)
        setupViews()
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        getAttributes(attrs, R.styleable.GroupPostFooterView) {
            val optionsVisible = getBoolean(R.styleable.GroupPostFooterView_optionsVisible, true)
            binding.lOptions.setIsVisible(optionsVisible)
        }
    }

    private fun setupViews() {
        with(binding) {
            btnReply.setOnClickListener {
                post?.let { listener?.onReply(it.id, it.postInfo.sender.disambiguatedDisplayName) }
            }
            btnShare.setOnClickListener {
                post?.let { listener?.onShare(it.content) }
            }
            btnLike.setOnClickListener {
                post?.let { listener?.onShowEmoji(it.id) }
            }
        }
    }

    fun setListener(postListener: GroupPostListener) {
        listener = postListener
    }

    fun setData(data: Post, isReply: Boolean) {
        post = data
        bindViewData(data.postInfo.timestamp, data.postInfo.isEncrypted, isReply)
    }

    fun bindViewData(timestamp: Long, isEncrypted: Boolean, isReply: Boolean) {
        with(binding) {
            btnReply.setIsVisible(!isReply)
            ivEncrypted.setIsEncryptedIcon(isEncrypted)
            tvMessageTime.text = DateFormat.getDateTimeInstance().format(Date(timestamp))
        }
    }

}