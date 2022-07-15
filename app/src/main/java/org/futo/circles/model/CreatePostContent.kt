package org.futo.circles.model

import android.net.Uri
import org.futo.circles.core.picker.MediaType

sealed class CreatePostContent()

data class TextPostContent(
    val text: String
) : CreatePostContent()

data class MediaPostContent(
    val uri: Uri,
    val mediaType: MediaType
) : CreatePostContent()
