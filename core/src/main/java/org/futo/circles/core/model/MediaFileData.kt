package org.futo.circles.core.model

import org.matrix.android.sdk.api.session.crypto.attachments.ElementToDecrypt

data class MediaFileData(
    val fileName: String,
    val mimeType: String,
    val fileUrl: String,
    val elementToDecrypt: ElementToDecrypt?
)