package org.futo.circles.gallery.model

import org.futo.circles.core.model.ConfirmationType
import org.futo.circles.gallery.R

data class DeleteGallery(
    override val titleRes: Int = R.string.delete_gallery,
    override val messageRes: Int = R.string.delete_gallery_message,
    override val positiveButtonRes: Int = R.string.delete
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)