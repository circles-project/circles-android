package org.futo.circles.core.model

import android.util.Size
import org.matrix.android.sdk.api.session.crypto.attachments.ElementToDecrypt

data class MediaFileData(
    val fileName: String,
    val mimeType: String,
    val fileUrl: String,
    val elementToDecrypt: ElementToDecrypt?,
    val width: Int,
    val height: Int,
    val duration: String
) {
    val aspectRatio = width.toFloat() / height.toFloat()
}