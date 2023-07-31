package org.futo.circles.gallery.model

import org.futo.circles.core.model.ConfirmationType
import org.futo.circles.gallery.R

data class DeleteGallery(
    override val titleRes: Int = R.string.delete_gallery,
    override val messageRes: Int = R.string.delete_gallery_message,
    override val positiveButtonRes: Int = R.string.delete
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)

data class RemoveImage(
    override val titleRes: Int = R.string.remove_image,
    override val messageRes: Int = R.string.remove_image_message,
    override val positiveButtonRes: Int =  org.futo.circles.core.R.string.remove
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)