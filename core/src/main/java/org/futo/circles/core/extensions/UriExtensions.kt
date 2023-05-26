package org.futo.circles.core.extensions

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.database.getStringOrNull

const val UriContentScheme = "content"

fun Uri.getFilename(context: Context): String? {
    if (scheme == UriContentScheme) {
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
