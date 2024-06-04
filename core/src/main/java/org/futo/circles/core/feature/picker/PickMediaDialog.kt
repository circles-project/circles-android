package org.futo.circles.core.feature.picker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialog
import org.futo.circles.core.databinding.DialogPickImageBinding
import org.futo.circles.core.extensions.setIsVisible


enum class PickImageMethod { Photo, Video, Device }

interface PickMediaDialogListener {
    fun onPickMethodSelected(method: PickImageMethod)
}

class PickMediaDialog(
    context: Context,
    private val isVideoAvailable: Boolean,
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

            tvPhoto.setOnClickListener { onMethodSelected(PickImageMethod.Photo) }
            tvVideo.setOnClickListener { onMethodSelected(PickImageMethod.Video) }
            tvDevice.setOnClickListener { onMethodSelected(PickImageMethod.Device) }
        }
    }

    private fun onMethodSelected(method: PickImageMethod) {
        listener.onPickMethodSelected(method)
        dismiss()
    }

}