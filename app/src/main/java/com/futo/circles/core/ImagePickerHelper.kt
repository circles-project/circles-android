package com.futo.circles.core

import android.app.Activity
import android.net.Uri
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.futo.circles.R
import com.futo.circles.extensions.getContentUriForFileUri
import com.futo.circles.extensions.showError
import com.github.dhaval2404.imagepicker.ImagePicker

class ImagePickerHelper(private val fragment: Fragment) : PickImageDialogListener {

    private val pickImageDialog by lazy {
        PickImageDialog(fragment.requireContext(), this)
    }

    private var onSelected: ((Int?, Uri) -> Unit)? = null
    private var itemId: Int? = null

    private val intent =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            val data = result.data
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    data?.data?.let { uri ->
                        val contentUri =
                            uri.getContentUriForFileUri(fragment.requireContext()) ?: run {
                                fragment.showError(fragment.getString(R.string.unexpected_error))
                                return@let
                            }
                        onSelected?.let { it(itemId, contentUri) }
                    } ?: fragment.showError(fragment.getString(R.string.unexpected_error))
                }
                ImagePicker.RESULT_ERROR -> fragment.showError(ImagePicker.getError(data))
            }
        }

    fun showImagePickerDialog(onImageSelected: (Int?, Uri) -> Unit, id: Int? = null) {
        itemId = id
        onSelected = onImageSelected
        pickImageDialog.show()
    }

    override fun onPickMethodSelected(method: PickImageMethod) {
        ImagePicker.with(fragment)
            .cropSquare()
            .apply { if (method == PickImageMethod.Camera) this.cameraOnly() else this.galleryOnly() }
            .createIntent { intent.launch(it) }
    }
}