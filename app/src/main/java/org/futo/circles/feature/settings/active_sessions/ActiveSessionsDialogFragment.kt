package org.futo.circles.feature.settings.active_sessions

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import org.futo.circles.R
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.ActiveSessionsDialogFragmentBinding
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.observeResponse
import org.futo.circles.extensions.showError
import org.futo.circles.feature.settings.active_sessions.list.ActiveSessionClickListener
import org.futo.circles.feature.settings.active_sessions.list.ActiveSessionsAdapter
import org.futo.circles.feature.settings.confirm_auth.ConfirmAuthDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class ActiveSessionsDialogFragment :
    BaseFullscreenDialogFragment(ActiveSessionsDialogFragmentBinding::inflate) {

    private val viewModel by viewModel<ActiveSessionsViewModel>()

    private val binding by lazy {
        getBinding() as ActiveSessionsDialogFragmentBinding
    }

    private var confirmAuthDialog: ConfirmAuthDialog? = null

    private val sessionsListAdapter by lazy {
        ActiveSessionsAdapter(object : ActiveSessionClickListener {
            override fun onItemClicked(deviceId: String) {
                viewModel.onSessionClicked(deviceId)
            }

            override fun onVerifySessionClicked(deviceId: String) {
                viewModel.verifySession(deviceId)
            }

            override fun onEnableCrossSigningClicked() {
                showEnableCrossSigningDialog()
            }

            override fun onRemoveSessionClicked(deviceId: String) {
                showRemoveSessionDialog(deviceId)
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
            rvSessions.apply {
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                adapter = sessionsListAdapter
            }
        }
    }

    private fun setupObservers() {
        viewModel.activeSessionsLiveData.observeData(this) {
            sessionsListAdapter.submitList(it)
        }
        viewModel.removeSessionLiveData.observeResponse(this,
            success = { confirmAuthDialog?.dismiss() },
            error = {
                confirmAuthDialog?.clearInput()
                showError(getString(R.string.invalid_auth))
            }
        )
        viewModel.enableCrossSigningLiveData.observeResponse(this,
            success = { confirmAuthDialog?.dismiss() },
            error = {
                confirmAuthDialog?.clearInput()
                showError(getString(R.string.invalid_auth))
            }
        )
        viewModel.verifySessionLiveData.observeResponse(this)
    }

    private fun showRemoveSessionDialog(deviceId: String) {
        confirmAuthDialog = ConfirmAuthDialog(
            context = requireContext(),
            message = getString(R.string.remove_session_message_format, deviceId),
            onConfirmed = { password -> viewModel.removeSession(deviceId, password) }
        ).apply {
            show()
            setOnDismissListener { confirmAuthDialog = null }
        }
    }

    private fun showEnableCrossSigningDialog() {
        confirmAuthDialog = ConfirmAuthDialog(
            context = requireContext(),
            message = getString(R.string.enable_cross_signing_message),
            onConfirmed = { password -> viewModel.enableCrossSigning(password) }
        ).apply {
            show()
            setOnDismissListener { confirmAuthDialog = null }
        }
    }
}