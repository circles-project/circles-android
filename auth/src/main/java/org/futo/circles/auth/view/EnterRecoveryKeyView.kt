package org.futo.circles.auth.view

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import org.futo.circles.auth.databinding.ViewEnterRecoveryKeyBinding
import org.futo.circles.core.extensions.getFilename
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.visible

class EnterRecoveryKeyView(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding = ViewEnterRecoveryKeyBinding.inflate(LayoutInflater.from(context), this)

    private var selectedFileUri: Uri? = null
    private var onFileUploadListener: (() -> Unit)? = null
    private var onInputChangedListener: (() -> Unit)? = null

    init {
        with(binding) {
            ivRemoveFile.setOnClickListener {
                selectedFileUri = null
                passPhraseGroup.visible()
                fileNameGroup.gone()
                handleOnInputChanged()
            }
            btnUploadFile.setOnClickListener {
                onFileUploadListener?.invoke()
            }
            tilRecoveryKey.editText?.doAfterTextChanged { handleOnInputChanged() }
        }
    }

    fun setup(
        onUploadFileListener: () -> Unit,
        onInputChanged: () -> Unit
    ) {
        onFileUploadListener = onUploadFileListener
        onInputChangedListener = onInputChanged
    }

    fun getRawKey(): String? = binding.tilRecoveryKey.editText?.text?.toString()
    fun getFileUri(): Uri? = selectedFileUri

    fun selectFile(uri: Uri) {
        selectedFileUri = uri
        binding.tvFileName.text = uri.getFilename(context) ?: uri.toString()
        binding.passPhraseGroup.gone()
        binding.fileNameGroup.visible()
        handleOnInputChanged()
    }

    private fun handleOnInputChanged() {
        onInputChangedListener?.invoke()
    }


}