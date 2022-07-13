package org.futo.circles.core.picker

import android.Manifest
import android.net.Uri
import androidx.fragment.app.Fragment
import org.futo.circles.core.PermissionHelper
import org.futo.circles.core.picker.device.PickDeviceMediaDialogFragment

class MediaPickerHelper(private val fragment: Fragment) : PickMediaDialogListener {

    private val pickMediaDialog by lazy {
        PickMediaDialog(fragment.requireContext(), this)
    }

    private val permissionHelper by lazy {
        PermissionHelper(
            fragment,
            onGranted = { showDevicePicker() }
        )
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
            PickImageMethod.Photo -> {}
            PickImageMethod.Video -> {}
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