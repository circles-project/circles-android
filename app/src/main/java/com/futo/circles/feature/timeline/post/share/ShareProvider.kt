package com.futo.circles.feature.timeline.post.share

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.futo.circles.R

object ShareProvider {

    fun share(context: Context, content: ShareableContent) {
        val intent = when (content) {
            is ImageShareable -> getImageShareIntent(content.uriToFile)
            is TextShareable -> getTextShareIntent(content.text)
        }
        context.startActivity(
            Intent.createChooser(intent, context.getString(R.string.share))
        )
    }

    private fun getTextShareIntent(message: String) = Intent().apply {
        action = Intent.ACTION_SEND
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        putExtra(Intent.EXTRA_TEXT, message)
        type = "text/plain"
    }

    private fun getImageShareIntent(fileUri: Uri) = Intent().apply {
        action = Intent.ACTION_SEND
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        putExtra(Intent.EXTRA_STREAM, fileUri)
        type = "image/*"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

}