package com.futo.circles.extensions

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.database.getStringOrNull

fun Uri.getFilename(context: Context): String? {
    if (scheme == "content") {
        context.contentResolver.query(this, null, null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    return try {
                        val index = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                        cursor.getStringOrNull(index)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }
            }
    }
    return path?.substringAfterLast('/')
}