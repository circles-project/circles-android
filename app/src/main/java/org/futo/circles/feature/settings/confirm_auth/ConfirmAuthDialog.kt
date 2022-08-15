package org.futo.circles.feature.settings.confirm_auth


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialog
import androidx.core.widget.doAfterTextChanged
import org.futo.circles.databinding.DialogConfirmAuthBinding
import org.futo.circles.extensions.getText

class ConfirmAuthDialog(
    context: Context,
    private val message: String,
    private val onConfirmed: (String) -> Unit
) :
    AppCompatDialog(context) {

    private val binding = DialogConfirmAuthBinding.inflate(LayoutInflater.from(context))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCancelable(false)

        with(binding) {
            tvMessage.text = message

            btnCancel.setOnClickListener {
                dismiss()
            }

            btnConfirm.setOnClickListener {
                btnConfirm.setIsLoading(true)
                onConfirmed(tilPassword.getText())
            }

            tilPassword.editText?.doAfterTextChanged {
                it?.let { btnConfirm.isEnabled = it.isNotEmpty() }
            }
        }
    }

    fun clearInput() {
        binding.btnConfirm.setIsLoading(false)
        binding.tilPassword.editText?.text?.clear()
    }
}