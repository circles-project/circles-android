package org.futo.circles.feature.settings.active_sessions.remove_session

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import org.futo.circles.R
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.databinding.RemoveSessionDialogFragmentBinding
import org.futo.circles.extensions.getText
import org.futo.circles.extensions.observeResponse
import org.futo.circles.extensions.showError
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RemoveSessionDialogFragment :
    BaseFullscreenDialogFragment(RemoveSessionDialogFragmentBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this
    private val args: RemoveSessionDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<RemoveSessionViewModel> { parametersOf(args.deviceId) }

    private val binding by lazy {
        getBinding() as RemoveSessionDialogFragmentBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
            tvRemoveSessionMessage.text =
                getString(R.string.remove_session_message_format, args.deviceId)
            btnRemove.setOnClickListener {
                viewModel.removeSession(tilPassword.getText())
                startLoading(btnRemove)
            }
            tilPassword.editText?.doAfterTextChanged {
                it?.let { btnRemove.isEnabled = tilPassword.getText().isNotEmpty() }
            }
        }
    }

    private fun setupObservers() {
        viewModel.removeSessionLiveData.observeResponse(this,
            success = { activity?.onBackPressed() },
            error = { showError(getString(R.string.invalid_auth)) }
        )
    }
}