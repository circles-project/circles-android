package org.futo.circles.core.picker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialog
import org.futo.circles.databinding.DialogPickImageBinding
import org.futo.circles.extensions.setIsVisible


enum class PickImageMethod { Photo, Video, Gallery, Device }

interface PickMediaDialogListener {
    fun onPickMethodSelected(method: PickImageMethod, allMediaTypeAvailable: Boolean)
}

class PickMediaDialog(
    context: Context,
    private val listener: PickMediaDialogListener,
    private val allMediaTypeAvailable: Boolean
) :
    AppCompatDialog(context) {

    private val binding = DialogPickImageBinding.inflate(LayoutInflater.from(context))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            btnClose.setOnClickListener { dismiss() }
            btnCancel.setOnClickListener { dismiss() }

            tvVideo.setIsVisible(allMediaTypeAvailable)

            tvPhoto.setOnClickListener { onMethodSelected(PickImageMethod.Photo) }
            tvVideo.setOnClickListener { onMethodSelected(PickImageMethod.Video) }
            tvGallery.setOnClickListener { onMethodSelected(PickImageMethod.Gallery) }
            tvDevice.setOnClickListener { onMethodSelected(PickImageMethod.Device) }
        }
    }

    private fun onMethodSelected(method: PickImageMethod) {
        listener.onPickMethodSelected(method, allMediaTypeAvailable)
        dismiss()
    }

}