package org.futo.circles.feature.timeline.post.create

import android.app.ActionBar
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialog
import androidx.core.widget.doAfterTextChanged
import org.futo.circles.core.extensions.getText
import org.futo.circles.databinding.DialogAddLinkBinding


class AddLinkDialog(context: Context, private val listener: (String?, String) -> Unit) :
    AppCompatDialog(context) {

    private val binding = DialogAddLinkBinding.inflate(LayoutInflater.from(context))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCancelable(false)
        window?.apply {
            setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 60))
            setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        }

        with(binding) {
            btnCancel.setOnClickListener { dismiss() }
            btnAdd.setOnClickListener {
                listener(
                    tilMessage.getText().takeIf { it.isNotEmpty() },
                    tilLink.getText()
                )
                dismiss()
            }
            tilLink.editText?.doAfterTextChanged {
                btnAdd.isEnabled = tilLink.getText().isNotEmpty()
            }
        }
    }
}