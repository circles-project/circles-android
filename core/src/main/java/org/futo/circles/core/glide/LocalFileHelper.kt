package org.futo.circles.core.glide

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import org.matrix.android.sdk.api.extensions.orFalse
import java.io.InputStream

class LocalFileHelper(private val context: Context) {

    fun isLocalFile(fileUri: String?): Boolean {
        return fileUri
            ?.let { Uri.parse(it) }
            ?.let { DocumentFile.fromSingleUri(context, it) }
            ?.exists()
            .orFalse()
    }

    fun openInputStream(fileUri: String?): InputStream? {
        return fileUri
            ?.takeIf { isLocalFile(it) }
            ?.let { Uri.parse(it) }
            ?.let { context.contentResolver.openInputStream(it) }
    }
}
