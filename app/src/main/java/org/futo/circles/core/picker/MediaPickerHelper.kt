package org.futo.circles.core.picker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import org.futo.circles.R
import org.futo.circles.core.utils.FileUtils.createImageFile
import org.futo.circles.core.utils.FileUtils.createVideoFile
import org.futo.circles.extensions.getUri
import org.futo.circles.extensions.showError
import org.matrix.android.sdk.api.util.MimeTypes.isMimeTypeImage
import java.io.File
import java.io.IOException

class GetContentWithMultiFilter : ActivityResultContracts.GetContent() {
    override fun createIntent(context: Context, input: String): Intent {
        val inputArray = input.split(";").toTypedArray()
        val myIntent = super.createIntent(context, "*/*")
        myIntent.putExtra(Intent.EXTRA_MIME_TYPES, inputArray)
        return myIntent
    }
}

enum class MediaType { Image, Video }

class MediaPickerHelper(
    private val fragment: Fragment,
    private val allMediaTypeAvailable: Boolean = false
) :
    PickMediaDialogListener {

    private val pickMediaDialog by lazy {
        PickMediaDialog(fragment.requireContext(), this, allMediaTypeAvailable)
    }

    private var mediaData: Pair<MediaType, Uri>? = null

    private val cameraIntentLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                mediaData?.let {
                    when (it.first) {
                        MediaType.Image -> onImageSelected?.invoke(itemId, it.second)
                        MediaType.Video -> onVideoSelected?.invoke(it.second)
                    }
                } ?: fragment.showError(fragment.getString(R.string.unexpected_error))
            }
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
            PickImageMethod.Photo -> dispatchCameraIntent(MediaType.Image)
            PickImageMethod.Video -> dispatchCameraIntent(MediaType.Video)
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
        deviceIntentLauncher.launch(mimeTypes.joinToString(";"))
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
                captureIntent.apply {
                    putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                cameraIntentLauncher.launch(captureIntent)
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