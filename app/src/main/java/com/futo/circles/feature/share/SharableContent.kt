package com.futo.circles.feature.share

import android.net.Uri

sealed class ShareableContent()

data class TextShareable(val text: String) : ShareableContent()
data class ImageShareable(val uriToFile: Uri) : ShareableContent()


