package org.futo.circles.core.picker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import org.futo.circles.R
import org.futo.circles.core.PermissionHelper
import org.futo.circles.core.picker.device.PickDeviceMediaDialogFragment
import org.futo.circles.extensions.getContentUriForFileUri
import org.futo.circles.extensions.showError

class MediaPickerHelper(private val fragment: Fragment) : PickMediaDialogListener {

    private val pickMediaDialog by lazy {
        PickMediaDialog(fragment.requireContext(), this)
    }

    private val permissionHelper = PermissionHelper(
        fragment,
        onGranted = { showDevicePicker() }
    )

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
            }
        }

    private var onSelected: ((Int?, Uri) -> Unit)? = null
    private var itemId: Int? = null


    fun showImagePickerDialog(onImageSelected: (Int?, Uri) -> Unit, id: Int? = null) {
        itemId = id
        onSelected = onImageSelected
        pickMediaDialog.show()
    }

    override fun onPickMethodSelected(method: PickImageMethod, allMediaTypeAvailable: Boolean) {
        when (method) {
            PickImageMethod.Photo -> intent.launch(Intent(MediaStore.ACTION_IMAGE_CAPTURE))
            PickImageMethod.Video -> intent.launch(Intent(MediaStore.ACTION_VIDEO_CAPTURE))
            PickImageMethod.Device -> permissionHelper.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            PickImageMethod.Gallery -> showGalleryPicker()
        }
    }

    private fun showGalleryPicker() {
        fragment.childFragmentManager.setFragmentResultListener(
            pickImageRequestKey,
            fragment
        ) { key, bundle ->
            if (key == pickImageRequestKey) {
                val uri = Uri.parse(bundle.getString(uriKey))
                onSelected?.invoke(itemId, uri)
            }
        }
        PickGalleryImageDialogFragment()
            .show(fragment.childFragmentManager, "PickGalleryImageDialogFragment")
    }

    private fun showDevicePicker() {
        fragment.childFragmentManager.setFragmentResultListener(
            pickImageRequestKey,
            fragment
        ) { key, bundle ->
            if (key == pickImageRequestKey) {
                val uri = Uri.parse(bundle.getString(uriKey))
                onSelected?.invoke(itemId, uri)
            }
        }

        PickDeviceMediaDialogFragment()
            .show(fragment.childFragmentManager, "PickDeviceMediaDialogFragment")
    }

    companion object {
        const val pickImageRequestKey = "pickImageRequestKey"
        const val uriKey = "uri"
    }
}