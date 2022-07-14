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
import org.futo.circles.core.FileUtils.createImageFile
import org.futo.circles.core.FileUtils.createVideoFile
import org.futo.circles.core.PermissionHelper
import org.futo.circles.core.picker.device.PickDeviceMediaDialogFragment
import org.futo.circles.extensions.getUri
import org.futo.circles.extensions.showError
import java.io.File
import java.io.IOException

enum class MediaType { Image, Video }

class MediaPickerHelper(private val fragment: Fragment, allMediaTypeAvailable: Boolean = false) :
    PickMediaDialogListener {

    private val pickMediaDialog by lazy {
        PickMediaDialog(fragment.requireContext(), this, allMediaTypeAvailable)
    }

    private val permissionHelper = PermissionHelper(
        fragment,
        onGranted = { showDevicePicker() }
    )
    private var mediaData: Pair<MediaType, Uri>? = null

    private val launcher =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            when (result.resultCode) {
                Activity.RESULT_OK -> {
                    mediaData?.let {
                        onSelected?.invoke(itemId, it.second, it.first)
                    } ?: fragment.showError(fragment.getString(R.string.unexpected_error))
                }
            }
        }

    private var onSelected: ((Int?, Uri, MediaType) -> Unit)? = null
    private var itemId: Int? = null


    fun showMediaPickerDialog(onMediaSelected: (Int?, Uri, MediaType) -> Unit, id: Int? = null) {
        itemId = id
        onSelected = onMediaSelected
        pickMediaDialog.show()
    }

    override fun onPickMethodSelected(method: PickImageMethod, allMediaTypeAvailable: Boolean) {
        when (method) {
            PickImageMethod.Photo -> dispatchMediaIntent(MediaType.Image)
            PickImageMethod.Video -> dispatchMediaIntent(MediaType.Video)
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
                onSelected?.invoke(itemId, uri, MediaType.Image)
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
                onSelected?.invoke(itemId, uri, MediaType.Image)
            }
        }

        PickDeviceMediaDialogFragment()
            .show(fragment.childFragmentManager, "PickDeviceMediaDialogFragment")
    }

    private fun dispatchMediaIntent(type: MediaType) {
        val context = fragment.context ?: return
        val intentAction = when (type) {
            MediaType.Image -> MediaStore.ACTION_IMAGE_CAPTURE
            MediaType.Video -> MediaStore.ACTION_VIDEO_CAPTURE
        }
        Intent(intentAction).also { captureIntent ->
            val file: File? = try {
                when (type) {
                    MediaType.Image -> createImageFile(context)
                    MediaType.Video -> createVideoFile(context)
                }
            } catch (ex: IOException) {
                null
            }
            file?.let {
                val uri: Uri = file.getUri(context).also { mediaData = type to it }
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                launcher.launch(captureIntent)
            }
        }
    }

    companion object {
        const val pickImageRequestKey = "pickImageRequestKey"
        const val uriKey = "uri"
    }
}