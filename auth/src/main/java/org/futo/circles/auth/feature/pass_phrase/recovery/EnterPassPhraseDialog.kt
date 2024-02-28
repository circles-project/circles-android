package org.futo.circles.auth.feature.pass_phrase.recovery

import android.app.ActionBar
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialog
import androidx.core.widget.doAfterTextChanged
import org.futo.circles.auth.databinding.DialogEnterPassphraseBinding
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.setIsVisible

class EnterPassPhraseDialog(context: Context, private val listener: EnterPassPhraseDialogListener) :
    AppCompatDialog(context) {

    private val binding = DialogEnterPassphraseBinding.inflate(LayoutInflater.from(context))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCancelable(false)
        window?.apply {
            setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 20))
            setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        }
        with(binding) {
            recoveryTypeGroup.setOnCheckedChangeListener { _, checkedId ->
                tilPassphrase.setIsVisible(checkedId == btnPassphrase.id)
                vRecoveryKey.setIsVisible(checkedId == btnRecoveryKey.id)
                handleRestoreButtonEnabled()
            }
            vRecoveryKey.setup(
                onUploadFileListener = { listener.onSelectFileClicked() },
                onInputChanged = { handleRestoreButtonEnabled() }
            )
            btnCancel.setOnClickListener {
                listener.onDoNotRestore()
                dismiss()
            }
            btnRestore.setOnClickListener {
                restoreBackup()
                dismiss()
            }
            tilPassphrase.editText?.doAfterTextChanged { handleRestoreButtonEnabled() }
        }
    }

    fun selectFile(uri: Uri) {
        binding.vRecoveryKey.selectFile(uri)
    }

    private fun restoreBackup() {
        if (isPassphraseTypeSelected()) {
            listener.onRestoreBackupWithPassphrase(binding.tilPassphrase.getText())
        } else {
            val uri = binding.vRecoveryKey.getFileUri()
            uri?.let {
                listener.onRestoreBackup(it)
            } ?: listener.onRestoreBackupWithRawKey(binding.vRecoveryKey.getRawKey() ?: "")
        }
    }

    private fun isPassphraseTypeSelected() =
        binding.recoveryTypeGroup.checkedRadioButtonId == binding.btnPassphrase.id

    private fun handleRestoreButtonEnabled() {
        binding.btnRestore.isEnabled = if (isPassphraseTypeSelected()) {
            binding.tilPassphrase.getText().isNotEmpty()
        } else {
            val isRawKeyEntered = binding.vRecoveryKey.getRawKey()?.isNotEmpty() == true
            val isFileKeyEntered = binding.vRecoveryKey.getFileUri() != null

            isRawKeyEntered || isFileKeyEntered
        }
    }
}