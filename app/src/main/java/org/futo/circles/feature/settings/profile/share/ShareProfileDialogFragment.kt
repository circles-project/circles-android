package org.futo.circles.feature.settings.profile.share

import android.os.Bundle
import android.view.View
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentShareProfileBinding
import org.futo.circles.extensions.gone
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.visible
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
        viewModel.qrProfileLiveData.observeData(this) { handelQrReady(it) }
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