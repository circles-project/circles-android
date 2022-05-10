package com.futo.circles.feature.following

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialog
import com.futo.circles.R
import com.futo.circles.databinding.RemoveFollowingDialogBinding

interface RemoveFollowingListener {
    fun onRemove(roomId: String)
    fun onUnfollow(roomId: String)
}

class RemoveFollowingDialog(
    context: Context,
    private val roomId: String,
    private val roomName: String,
    private val listener: RemoveFollowingListener
) : AppCompatDialog(context) {

    private val binding = RemoveFollowingDialogBinding.inflate(LayoutInflater.from(context))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            val title = "${context.getString(R.string.remove)} $roomName"
            tvTitle.text = title
            btnClose.setOnClickListener { dismiss() }
            btnCancel.setOnClickListener { dismiss() }

            tvRemove.setOnClickListener {
                listener.onRemove(roomId)
                dismiss()
            }
            tvUnfollow.setOnClickListener {
                listener.onUnfollow(roomId)
                dismiss()
            }
        }
    }

}