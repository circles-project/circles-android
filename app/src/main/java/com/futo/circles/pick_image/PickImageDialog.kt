package com.futo.circles.pick_image

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialog
import com.futo.circles.databinding.PickImageDialogBinding


enum class PickImageMethod { Camera, Gallery }

interface PickImageDialogListener {
    fun onPickMethodSelected(method: PickImageMethod)
}

class PickImageDialog(context: Context, private val listener: PickImageDialogListener) :
    AppCompatDialog(context) {

    private val binding = PickImageDialogBinding.inflate(LayoutInflater.from(context))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            btnClose.setOnClickListener { dismiss() }
            btnCancel.setOnClickListener { dismiss() }

            tvCamera.setOnClickListener {
                listener.onPickMethodSelected(PickImageMethod.Camera)
                dismiss()
            }
            tvGallery.setOnClickListener {
                listener.onPickMethodSelected(PickImageMethod.Gallery)
                dismiss()
            }
        }

    }

}