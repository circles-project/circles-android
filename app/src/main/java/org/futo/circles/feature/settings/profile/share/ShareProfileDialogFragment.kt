package org.futo.circles.feature.settings.profile.share

import android.os.Bundle
import android.view.View
import org.futo.circles.R
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.visible
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentShareProfileBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ShareProfileDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentShareProfileBinding::inflate) {

    private val viewModel by viewModel<ShareProfileViewModel>()

    private val binding by lazy {
        getBinding() as DialogFragmentShareProfileBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObServers()
    }

    private fun setupObServers() {
        viewModel.qrProfileLiveData.observeData(this) {
            it?.let { handelQrReady(it) } ?: run {
                binding.vLoading.gone()
                showError(getString(R.string.shared_circles_space_not_found))
            }
        }
    }

    private fun handelQrReady(qrText: String) {
        with(binding) {
            vLoading.gone()
            ivQr.visible()
            ivQr.setData(qrText)
            tvMessage.text = qrText
        }
    }
}