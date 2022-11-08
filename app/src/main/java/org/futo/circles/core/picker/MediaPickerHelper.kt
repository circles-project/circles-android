package org.futo.circles.core.picker

import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import org.futo.circles.R
import org.futo.circles.core.utils.FileUtils.createImageFile
import org.futo.circles.core.utils.FileUtils.createVideoFile
import org.futo.circles.extensions.getUri
import org.futo.circles.extensions.showError
import org.matrix.android.sdk.api.util.MimeTypes.isMimeTypeImage


class MediaPickerHelper(
    private val fragment: Fragment,
    private val allMediaTypeAvailable: Boolean = false
) : PickMediaDialogListener {

    private val pickMediaDialog by lazy {
        PickMediaDialog(fragment.requireContext(), this, allMediaTypeAvailable)
    }

    private val cameraPermissionHelper = CameraPermissionHelper(fragment)
    private var cameraUri: Uri? = null

    private val photoIntentLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.TakePicture())
        { success ->
            if (!success) return@registerForActivityResult
            cameraUri?.let {
                onImageSelected?.invoke(itemId, it)
            } ?: fragment.showError(fragment.getString(R.string.unexpected_error))
        }

    private val videoIntentLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.CaptureVideo())
        { success ->
            if (!success) return@registerForActivityResult
            cameraUri?.let {
                onVideoSelected?.invoke(it)
            } ?: fragment.showError(fragment.getString(R.string.unexpected_error))
        }

    private val deviceIntentLauncher = fragment.registerForActivityResult(
        GetContentWithMultiFilter()
    ) { uri ->
        uri ?: return@registerForActivityResult

        val mimeType = fragment.context?.contentResolver?.getType(uri)
        if (mimeType.isMimeTypeImage()) onImageSelected?.invoke(itemId, uri)
        else onVideoSelected?.invoke(uri)
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

    override fun onPickMethodSelected(method: PickImageMethod) {
        when (method) {
            PickImageMethod.Photo -> cameraPermissionHelper.runWithCameraPermission {
                dispatchCameraIntent(MediaType.Image)
            }
            PickImageMethod.Video -> cameraPermissionHelper.runWithCameraPermission {
                dispatchCameraIntent(MediaType.Video)
            }
            PickImageMethod.Device -> dispatchDevicePickerIntent()
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
            .show(fragment.childFragmentManager, "PickGalleryMediaDialogFragment")
    }

    private fun dispatchDevicePickerIntent() {
        val mimeTypes = mutableListOf("image/*")
        if (allMediaTypeAvailable) mimeTypes.add("video/*")
        try {
            deviceIntentLauncher.launch(mimeTypes.joinToString(";"))
        } catch (e: Exception) {
            handleException(e)
        }
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

    private fun dispatchCameraIntent(type: MediaType) {
        val context = fragment.context ?: return
        try {
            cameraUri = when (type) {
                MediaType.Image -> {
                    val imageFileUri = createImageFile(context).getUri(context)
                    photoIntentLauncher.launch(imageFileUri)
                    imageFileUri
                }
                MediaType.Video -> {
                    val videoFileUri = createVideoFile(context).getUri(context)
                    videoIntentLauncher.launch(videoFileUri)
                    videoFileUri
                }
            }
        } catch (e: Exception) {
            handleException(e)
        }
    }

    private fun handleException(e: Exception) {
        val message = when (e) {
            is ActivityNotFoundException -> fragment.getString(R.string.no_application_found_for_action)
            else -> e.message ?: fragment.getString(R.string.unexpected_error)
        }
        fragment.showError(message)
    }


    companion object {
        const val IS_VIDEO_AVAILABLE = "IsVideoAvailable"
        const val pickMediaRequestKey = "pickMediaRequestKey"
        const val uriKey = "uri"
        const val mediaTypeKey = "mediaType"
    }
}