package org.futo.circles.feature.log_in

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialog
import androidx.core.widget.doAfterTextChanged
import org.futo.circles.databinding.DialogEnterPassphraseBinding
import org.futo.circles.extensions.getFilename
import org.futo.circles.extensions.getText
import org.futo.circles.extensions.gone
import org.futo.circles.extensions.visible

interface EnterPassPhraseDialogListener {
    fun onRestoreBackup(passphrase: String)
    fun onRestoreBackup(uri: Uri)
    fun onDoNotRestore()
    fun onSelectFileClicked()
}

class EnterPassPhraseDialog(context: Context, private val listener: EnterPassPhraseDialogListener) :
    AppCompatDialog(context) {

    private val binding = DialogEnterPassphraseBinding.inflate(LayoutInflater.from(context))

    private var selectedFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCancelable(false)

        with(binding) {
            fileNameGroup.gone()
            btnCancel.setOnClickListener {
                listener.onDoNotRestore()
                dismiss()
            }
            btnRestore.setOnClickListener {
                selectedFileUri?.let {
                    listener.onRestoreBackup(it)
                } ?: listener.onRestoreBackup(tilPassphrase.getText())
                dismiss()
            }
            tilPassphrase.editText?.doAfterTextChanged { handleRestoreButtonEnabled() }
            ivRemoveFile.setOnClickListener {
                selectedFileUri = null
                binding.passPhraseGroup.visible()
                binding.fileNameGroup.gone()
                handleRestoreButtonEnabled()
            }
            btnUploadFile.setOnClickListener {
                listener.onSelectFileClicked()
            }
        }
    }

    fun selectFile(uri: Uri) {
        selectedFileUri = uri
        binding.tvFileName.text = uri.getFilename(context) ?: uri.toString()
        binding.passPhraseGroup.gone()
        binding.fileNameGroup.visible()
        handleRestoreButtonEnabled()
    }

    private fun handleRestoreButtonEnabled() {
        binding.btnRestore.isEnabled =
            binding.tilPassphrase.getText().isNotEmpty() || selectedFileUri != null
    }
}