package org.futo.circles.core.model

import android.net.Uri
import org.matrix.android.sdk.api.session.crypto.attachments.ElementToDecrypt

data class MediaFileData(
    val fileName: String,
    val mimeType: String,
    val fileUrl: String,
    val elementToDecrypt: ElementToDecrypt?,
    val width: Int,
    val height: Int,
    val duration: String,
    val videoUri: Uri? = null
)