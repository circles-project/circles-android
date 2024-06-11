package org.futo.circles.auth.feature.setup.circles.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialog
import androidx.core.widget.doAfterTextChanged
import org.futo.circles.auth.databinding.DialogAddSetupCirclesItemBinding
import org.futo.circles.core.extensions.getText


class AddSetupCirclesItemDialog(context: Context, private val listener: (String) -> Unit) :
    AppCompatDialog(context) {

    private val binding = DialogAddSetupCirclesItemBinding.inflate(LayoutInflater.from(context))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCancelable(false)

        with(binding) {
            btnCancel.setOnClickListener { dismiss() }
            btnAdd.setOnClickListener {
                listener(tilName.getText())
                dismiss()
            }
            tilName.editText?.doAfterTextChanged {
                btnAdd.isEnabled = tilName.getText().isNotEmpty()
            }
        }
    }
}