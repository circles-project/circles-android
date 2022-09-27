package org.futo.circles.extensions

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import org.futo.circles.core.FILE_PROVIDER_AUTHORITY_EXTENSION
import java.io.File

fun File.getUri(context: Context): Uri = FileProvider.getUriForFile(
    context, context.packageName + FILE_PROVIDER_AUTHORITY_EXTENSION, this
)