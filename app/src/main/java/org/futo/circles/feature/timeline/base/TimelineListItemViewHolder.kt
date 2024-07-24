package org.futo.circles.feature.timeline.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.futo.circles.R
import org.futo.circles.core.model.PostListItem
import org.futo.circles.feature.timeline.InternalLinkMovementMethod
import org.futo.circles.feature.timeline.list.OnLinkClickedListener
import org.matrix.android.sdk.api.extensions.tryOrNull

abstract class TimelineListItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(item: PostListItem)


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