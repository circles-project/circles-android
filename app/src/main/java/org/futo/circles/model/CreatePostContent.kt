package org.futo.circles.model

import android.net.Uri
import org.futo.circles.core.model.MediaType

sealed class CreatePostContent

data class TextPostContent(
    val text: String
) : CreatePostContent()

data class MediaPostContent(
    val caption: String?,
    val uri: Uri,
    val mediaType: MediaType
) : CreatePostContent()
