package org.futo.circles.model

import android.net.Uri

sealed class CreatePostContent()

data class TextPostContent(
    val text: String
) : CreatePostContent()

data class ImagePostContent(
    val uri: Uri
) : CreatePostContent()
