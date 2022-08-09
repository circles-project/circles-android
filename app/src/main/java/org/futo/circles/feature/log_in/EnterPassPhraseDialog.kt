package org.futo.circles.feature.log_in

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialog
import androidx.core.widget.doAfterTextChanged
import org.futo.circles.databinding.EnterPassphraseDialogBinding
import org.futo.circles.extensions.getText

interface EnterPassPhraseDialogListener {
    fun onRestoreBackup(passphrase: String)
    fun onDoNotRestore()
}

class EnterPassPhraseDialog(context: Context, private val listener: EnterPassPhraseDialogListener) :
    AppCompatDialog(context) {

    private val binding = EnterPassphraseDialogBinding.inflate(LayoutInflater.from(context))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCancelable(false)

        with(binding) {

            btnCancel.setOnClickListener {
                listener.onDoNotRestore()
                dismiss()
            }

            btnRestore.setOnClickListener {
                listener.onRestoreBackup(tilPassphrase.getText())
                dismiss()
            }

            tilPassphrase.editText?.doAfterTextChanged {
                it?.let { btnRestore.isEnabled = it.isNotEmpty() }
            }
        }
    }
}