package com.futo.circles.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.futo.circles.R
import com.futo.circles.databinding.GroupPostViewBinding
import com.futo.circles.extensions.dimen
import com.futo.circles.extensions.loadMatrixThumbnail
import com.futo.circles.extensions.setVisibility
import com.futo.circles.ui.groups.timeline.model.*
import org.matrix.android.sdk.api.session.content.ContentUrlResolver


class GroupPostView(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {

    private val binding =
        GroupPostViewBinding.inflate(LayoutInflater.from(context), this)

    private var content: View? = null

    fun setData(data: GroupMessage, urlResolver: ContentUrlResolver?) {
        setGeneralMessageData(data.generalMessageInfo, urlResolver)
        setContent(data, urlResolver)
    }

    private fun setGeneralMessageData(
        data: GroupGeneralMessageInfo,
        urlResolver: ContentUrlResolver?
    ) {
        binding.vReplyMargin.setVisibility(data.isReply())
        binding.postHeader.setData(data.sender, urlResolver)
        binding.postFooter.setData(data)
    }

    private fun setContent(data: GroupMessage, urlResolver: ContentUrlResolver?) {
        clearContent()
        when (data.type) {
            GroupMessageType.TEXT_MESSAGE -> setTextContent(data as? GroupTextMessage)
            GroupMessageType.IMAGE_MESSAGE -> setImageContent(
                data as? GroupImageMessage,
                urlResolver
            )
        }
    }

    private fun clearContent() {
        binding.lvContent.removeAllViews()
        content = null
    }

    private fun setTextContent(data: GroupTextMessage?) {
        addTextViewContent()
        (content as? TextView)?.text = data?.message
    }

    private fun setImageContent(data: GroupImageMessage?, urlResolver: ContentUrlResolver?) {
        addImageViewContent()
        (content as? ImageView)?.loadMatrixThumbnail(data?.encryptedImageUrl, urlResolver)
    }

    private fun addImageViewContent() {
        content = ImageView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        binding.lvContent.addView(content)
    }

    private fun addTextViewContent() {
        content = TextView(context).apply {
            layoutParams =
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                    val sideMargin = context.dimen(R.dimen.post_text_side_margin)
                    setMargins(sideMargin, 0, sideMargin, 0)
                }
            setTextAppearance(R.style.body)
        }
        binding.lvContent.addView(content)
    }
}