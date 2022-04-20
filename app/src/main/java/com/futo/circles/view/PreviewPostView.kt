package com.futo.circles.view


import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.futo.circles.databinding.PreviewPostViewBinding
import com.futo.circles.model.Post


class PreviewPostView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        PreviewPostViewBinding.inflate(LayoutInflater.from(context), this)


    fun setData(data: Post) {
        binding.postHeader.setData(data.postInfo.sender)
        binding.postFooter.setData(data.postInfo, false)
    }

}