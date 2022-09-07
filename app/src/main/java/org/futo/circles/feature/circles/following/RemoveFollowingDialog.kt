package org.futo.circles.feature.circles.following

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialog
import org.futo.circles.R
import org.futo.circles.databinding.DialogRemoveFollowingBinding
import org.futo.circles.extensions.setIsVisible

interface RemoveFollowingListener {
    fun onRemove(roomId: String)
    fun onUnfollow(roomId: String)
}

class RemoveFollowingDialog(
    context: Context,
    private val roomId: String,
    private val roomName: String,
    private val followInCirclesCount: Int,
    private val listener: RemoveFollowingListener
) : AppCompatDialog(context) {

    private val binding = DialogRemoveFollowingBinding.inflate(LayoutInflater.from(context))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            val title = "${context.getString(R.string.remove)} $roomName"
            tvTitle.text = title
            btnClose.setOnClickListener { dismiss() }
            btnCancel.setOnClickListener { dismiss() }

            tvRemove.setIsVisible(followInCirclesCount > 1)
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