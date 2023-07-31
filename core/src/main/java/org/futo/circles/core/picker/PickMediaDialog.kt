package org.futo.circles.core.picker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialog
import org.futo.circles.core.databinding.DialogPickImageBinding
import org.futo.circles.core.extensions.setIsVisible


enum class PickImageMethod { Photo, Video, Gallery, Device }

interface PickMediaDialogListener {
    fun onPickMethodSelected(method: PickImageMethod)
}

class PickMediaDialog(
    context: Context,
    private val isVideoAvailable: Boolean,
    private val isGalleryAvailable: Boolean,
    private val listener: PickMediaDialogListener
) : AppCompatDialog(context) {

    private val binding = DialogPickImageBinding.inflate(LayoutInflater.from(context))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            btnClose.setOnClickListener { dismiss() }
            btnCancel.setOnClickListener { dismiss() }

            tvVideo.setIsVisible(isVideoAvailable)
            tvGallery.setIsVisible(isGalleryAvailable)

            tvPhoto.setOnClickListener { onMethodSelected(PickImageMethod.Photo) }
            tvVideo.setOnClickListener { onMethodSelected(PickImageMethod.Video) }
            tvGallery.setOnClickListener { onMethodSelected(PickImageMethod.Gallery) }
            tvDevice.setOnClickListener { onMethodSelected(PickImageMethod.Device) }
        }
    }

    private fun onMethodSelected(method: PickImageMethod) {
        listener.onPickMethodSelected(method)
        dismiss()
    }

}