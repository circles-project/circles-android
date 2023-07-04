package org.futo.circles.core.picker

import android.Manifest
import android.content.ActivityNotFoundException
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import org.futo.circles.core.R
import org.futo.circles.core.extensions.getUri
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.utils.FileUtils.createImageFile
import org.futo.circles.core.utils.FileUtils.createVideoFile
import org.matrix.android.sdk.api.util.MimeTypes.isMimeTypeImage


open class DeviceMediaPickerHelper(
    private val fragment: Fragment,
    private val allMediaTypeAvailable: Boolean = false
) : PickMediaDialogListener {

    override val isVideoAvailable: Boolean = allMediaTypeAvailable
    override val isGalleryAvailable: Boolean = false

    private val pickMediaDialog by lazy {
        PickMediaDialog(fragment.requireContext(), this)
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

    private val deviceIntentLauncher = fragment.registerForActivityResult(
        GetContentWithMultiFilter()
    ) { uri ->
        uri ?: return@registerForActivityResult

        val mimeType = fragment.context?.contentResolver?.getType(uri)
        if (mimeType.isMimeTypeImage()) onImageSelected?.invoke(itemId, uri)
        else onVideoSelected?.invoke(uri)
    }

    protected var onImageSelected: ((Int?, Uri) -> Unit)? = null
    protected var onVideoSelected: ((Uri) -> Unit)? = null
    protected var itemId: Int? = null

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
            PickImageMethod.Gallery -> onGalleryMethodSelected()
        }
    }

    protected open fun onGalleryMethodSelected() {
        throw IllegalArgumentException("Gallery method is not available")
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