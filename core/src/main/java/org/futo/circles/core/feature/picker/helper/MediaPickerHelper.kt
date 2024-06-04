package org.futo.circles.core.feature.picker.helper

import android.Manifest
import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import org.futo.circles.core.R
import org.futo.circles.core.extensions.getUri
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.model.MediaType
import org.futo.circles.core.model.PickGalleryMediaResultItem
import org.futo.circles.core.feature.picker.PickImageMethod
import org.futo.circles.core.feature.picker.PickMediaDialog
import org.futo.circles.core.feature.picker.PickMediaDialogListener
import org.futo.circles.core.feature.picker.gallery.PickGalleryMediaDialogFragment
import org.futo.circles.core.utils.FileUtils.createImageFile
import org.futo.circles.core.utils.FileUtils.createVideoFile
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.util.MimeTypes.isMimeTypeImage


open class MediaPickerHelper(
    private val fragment: Fragment,
    private val isMultiSelect: Boolean = false,
    private val isVideoAvailable: Boolean = false
) : PickMediaDialogListener {


    private val pickMediaDialog by lazy {
        PickMediaDialog(fragment.requireContext(), isVideoAvailable, this)
    }

    private val cameraPermissionHelper =
        RuntimePermissionHelper(fragment, Manifest.permission.CAMERA)

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

    private val deviceMultiselectIntentLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia()
    ) { uriList ->
        uriList.forEach { uri -> onMediaFromDeviceSelected(uri) }
    }

    private val deviceIntentLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { onMediaFromDeviceSelected(uri) }
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
            PickImageMethod.Photo -> cameraPermissionHelper.runWithPermission {
                dispatchCameraIntent(MediaType.Image)
            }

            PickImageMethod.Video -> cameraPermissionHelper.runWithPermission {
                dispatchCameraIntent(MediaType.Video)
            }

            PickImageMethod.Device -> dispatchDevicePickerIntent()
        }
    }

    private fun onMediaFromDeviceSelected(uri: Uri) {
        val mimeType = fragment.context?.contentResolver?.getType(uri)
        if (mimeType.isMimeTypeImage()) onImageSelected?.invoke(itemId, uri)
        else onVideoSelected?.invoke(uri)
    }

    private fun dispatchDevicePickerIntent() {
        val request = PickVisualMediaRequest(
            if (isVideoAvailable) ActivityResultContracts.PickVisualMedia.ImageAndVideo
            else ActivityResultContracts.PickVisualMedia.ImageOnly
        )
        try {
            if (isMultiSelect) deviceMultiselectIntentLauncher.launch(request)
            else deviceIntentLauncher.launch(request)
        } catch (e: Exception) {
            handleException(e)
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
        const val pickMediaRequestKey = "pickMediaRequestKey"
        const val pickMediaResultDataKey = "pickMediaResultDataKey"
    }
}