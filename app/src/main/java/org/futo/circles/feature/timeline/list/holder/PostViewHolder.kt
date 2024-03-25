package org.futo.circles.feature.timeline.list.holder

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.futo.circles.R
import org.futo.circles.core.model.Post
import org.futo.circles.feature.timeline.InternalLinkMovementMethod
import org.futo.circles.feature.timeline.list.OnLinkClickedListener
import org.futo.circles.model.PostItemPayload
import org.futo.circles.view.PostLayout
import org.matrix.android.sdk.api.extensions.tryOrNull


sealed class PostViewHolder(view: View, private val isThread: Boolean) :
    RecyclerView.ViewHolder(view) {

    abstract val postLayout: PostLayout

    @CallSuper
    open fun bind(post: Post) {
        postLayout.setData(post, isThread)
    }

    fun bindPayload(payload: PostItemPayload) {
        postLayout.setPayload(payload)
    }


    @SuppressLint("ClickableViewAccessibility")
    protected fun handleLinkClick(textView: TextView) {
        textView.apply {
            movementMethod = InternalLinkMovementMethod(object : OnLinkClickedListener {
                override fun onLinkClicked(url: String) {
                    showLinkConfirmation(context, url)
                }
            })
            setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) v.requestFocus()
                false
            }
        }
    }

    private fun showLinkConfirmation(context: Context, url: String) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.do_you_want_to_open_this_url)
            .setMessage(url)
            .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                tryOrNull {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
                dialogInterface.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

}


