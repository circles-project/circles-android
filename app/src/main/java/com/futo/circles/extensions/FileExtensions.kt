package com.futo.circles.extensions

import android.content.Context
import androidx.core.content.FileProvider
import com.futo.circles.core.FILE_PROVIDER_AUTHORITY_PREFIX
import java.io.File

fun File.getUri(context: Context) = FileProvider.getUriForFile(
    context, context.packageName + FILE_PROVIDER_AUTHORITY_PREFIX, this
)