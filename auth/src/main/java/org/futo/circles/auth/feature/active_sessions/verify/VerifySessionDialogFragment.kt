package org.futo.circles.auth.feature.active_sessions.verify

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.DialogFragmentVerifySessionBinding
import org.futo.circles.auth.feature.active_sessions.verify.qr.QrScannerActivity
import org.futo.circles.auth.model.QrCanceled
import org.futo.circles.auth.model.QrLoading
import org.futo.circles.auth.model.QrReady
import org.futo.circles.auth.model.QrSuccess
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.extensions.visible
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.feature.picker.helper.RuntimePermissionHelper

@AndroidEntryPoint
class VerifySessionDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentVerifySessionBinding::inflate) {

    private val viewModel by viewModels<VerifySessionViewModel>()

    private val binding by lazy {
        getBinding() as DialogFragmentVerifySessionBinding
    }

    private val scanActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val scannedQrCode = QrScannerActivity.getResultText(activityResult.data)
                viewModel.onQrScanned(scannedQrCode)
            }
        }

    private val cameraPermissionHelper = RuntimePermissionHelper(this, Manifest.permission.CAMERA)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObServers()
    }

    private fun setupViews() {
        binding.btnVerify.setOnClickListener {
            cameraPermissionHelper.runWithPermission {
                QrScannerActivity.startForResult(requireActivity(), scanActivityResultLauncher)
            }
        }
    }

    private fun setupObServers() {
        viewModel.qrStateLiveData.observeData(this) { qrState ->
            when (qrState) {
                is QrCanceled -> {
                    showError(qrState.reason)
                    view?.postDelayed({ onBackPressed() }, CLOSE_DELAY)
                }

                is QrLoading -> handelQrLoading(qrState.deviceId, qrState.isCurrentSessionVerified)
                is QrReady -> handelQrReady(qrState.qrText)
                is QrSuccess -> {
                    showSuccess(getString(R.string.session_verified))
                    view?.postDelayed({ onBackPressed() }, CLOSE_DELAY)
                }
            }
        }
    }

    private fun handelQrReady(qrText: String) {
        with(binding) {
            vLoading.gone()
            btnVerify.isEnabled = true
            ivQr.visible()
            ivQr.setData(qrText)
            tvMessage.text = getString(R.string.scan_with_one_of_devices_message)
        }
    }

    private fun handelQrLoading(deviceId: String, isSessionVerified: Boolean) {
        with(binding) {
            vLoading.visible()
            ivQr.visibility = View.INVISIBLE
            btnVerify.isEnabled = false
            val sessionName = if (isSessionVerified) deviceId else getString(R.string.cross_signed)
            tvMessage.text = getString(R.string.waiting_for_verification_format, sessionName)
        }
    }

    companion object {
        private const val CLOSE_DELAY = 1500L
    }
}