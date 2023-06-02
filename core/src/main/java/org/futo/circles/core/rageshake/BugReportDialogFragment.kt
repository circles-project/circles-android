package org.futo.circles.core.rageshake

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import org.futo.circles.core.R
import org.futo.circles.core.databinding.DialogFragmentBugReportBinding
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.isValidEmail
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.fragment.HasLoadingState
import org.koin.androidx.viewmodel.ext.android.viewModel

class BugReportDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentBugReportBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModel<BugReportViewModel>()
    private val binding by lazy {
        getBinding() as DialogFragmentBugReportBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            toolbar.apply { setNavigationOnClickListener { dismiss() } }
            svScreenshot.setOnCheckedChangeListener { _, isChecked ->
                ivScreenshot.setIsVisible(isChecked)
            }
            tilDescription.editText?.doAfterTextChanged { validateInput() }
            tilContactInfo.editText?.doAfterTextChanged { validateInput() }
            lSendLogs.setOnClickListener { svSendLogs.isChecked = !svSendLogs.isChecked }
            lScreenshot.setOnClickListener { svScreenshot.isChecked = !svScreenshot.isChecked }
            btnReport.setOnClickListener {
                startLoading(binding.btnReport)
                viewModel.sendReport(
                    tilDescription.getText(),
                    tilContactInfo.getText(), svSendLogs.isChecked, svScreenshot.isChecked
                )
            }
        }
    }

    private fun setupObservers() {
        viewModel.threePidLiveData?.observeData(this) {
            binding.tilContactInfo.editText?.setText(it.firstOrNull()?.value ?: "")
        }
        viewModel.sendReportLiveData.observeResponse(this,
            success = {
                showSuccess(getString(R.string.report_sent), true)
                dismiss()
            }
        )
        viewModel.screenshotLiveData.observeData(this) { bitmap ->
            binding.lScreenshot.setIsVisible(bitmap != null)
            binding.svScreenshot.isChecked = bitmap != null
            bitmap?.let { binding.ivScreenshot.setImageBitmap(bitmap) }
        }
    }

    private fun validateInput() {
        binding.btnReport.isEnabled = binding.tilDescription.getText().isNotEmpty() &&
                binding.tilContactInfo.getText().isValidEmail()
    }

    companion object {
        fun show(activity: AppCompatActivity) =
            BugReportDialogFragment().show(
                activity.supportFragmentManager, "BugReportDialogFragment"
            )
    }
}