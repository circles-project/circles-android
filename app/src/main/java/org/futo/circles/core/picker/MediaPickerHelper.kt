package org.futo.circles.core.picker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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

class MediaPickerHelper(
    private val fragment: Fragment,
    private val allMediaTypeAvailable: Boolean = false
) :
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
                        when (it.first) {
                            MediaType.Image -> onImageSelected?.invoke(itemId, it.second)
                            MediaType.Video -> onVideoSelected?.invoke(it.second)
                        }
                    } ?: fragment.showError(fragment.getString(R.string.unexpected_error))
                }
            }
        }

    private var onImageSelected: ((Int?, Uri) -> Unit)? = null
    private var onVideoSelected: ((Uri) -> Unit)? = null
    private var itemId: Int? = null

    fun showMediaPickerDialog(
        onImageSelected: (Int?, Uri) -> Unit,
        onVideoSelected: (Uri) -> Unit = {},
        id: Int? = null
    ) {
        itemId = id
        this.onImageSelected = onImageSelected
        this.onVideoSelected = onVideoSelected
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
            pickMediaRequestKey,
            fragment
        ) { key, bundle -> handlePickerFragmentResult(key, bundle) }

        PickGalleryMediaDialogFragment
            .create(allMediaTypeAvailable)
            .show(fragment.childFragmentManager, "PickGalleryImageDialogFragment")
    }

    private fun showDevicePicker() {
        fragment.childFragmentManager.setFragmentResultListener(
            pickMediaRequestKey,
            fragment
        ) { key, bundle -> handlePickerFragmentResult(key, bundle) }

        PickDeviceMediaDialogFragment
            .create(allMediaTypeAvailable)
            .show(fragment.childFragmentManager, "PickDeviceMediaDialogFragment")
    }

    private fun handlePickerFragmentResult(key: String, bundle: Bundle) {
        if (key == pickMediaRequestKey) {
            val uri = Uri.parse(bundle.getString(uriKey))
            val typeOrdinal = bundle.getInt(mediaTypeKey)
            when (MediaType.values()[typeOrdinal]) {
                MediaType.Image -> onImageSelected?.invoke(itemId, uri)
                MediaType.Video -> onVideoSelected?.invoke(uri)
            }
        }
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
        const val IS_VIDEO_AVAILABLE = "IsVideoAvailable"
        const val pickMediaRequestKey = "pickMediaRequestKey"
        const val uriKey = "uri"
        const val mediaTypeKey = "mediaType"
    }
}