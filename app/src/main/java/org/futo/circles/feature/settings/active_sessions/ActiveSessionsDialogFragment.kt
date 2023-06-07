package org.futo.circles.feature.settings.active_sessions

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentActiveSessionsBinding
import org.futo.circles.feature.settings.active_sessions.list.ActiveSessionClickListener
import org.futo.circles.feature.settings.active_sessions.list.ActiveSessionsAdapter
import org.futo.circles.model.RemoveSession
import org.futo.circles.model.ResetKeys

@AndroidEntryPoint
class ActiveSessionsDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentActiveSessionsBinding::inflate) {

    private val viewModel by viewModels<ActiveSessionsViewModel>()

    private val binding by lazy {
        getBinding() as DialogFragmentActiveSessionsBinding
    }

    private val sessionsListAdapter by lazy {
        ActiveSessionsAdapter(object : ActiveSessionClickListener {
            override fun onItemClicked(deviceId: String) {
                viewModel.onSessionClicked(deviceId)
            }

            override fun onVerifySessionClicked(deviceId: String) {
                findNavController().navigate(
                    ActiveSessionsDialogFragmentDirections.toVerifySessionDialogFragment(
                        deviceId
                    )
                )
            }

            override fun onResetKeysClicked() {
                withConfirmation(ResetKeys()) { viewModel.resetKeysToEnableCrossSigning() }
            }

            override fun onRemoveSessionClicked(deviceId: String) {
                withConfirmation(RemoveSession()) { viewModel.removeSession(deviceId) }
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
            error = { showError(getString(R.string.invalid_auth)) }
        )
        viewModel.resetKeysLiveData.observeResponse(this,
            error = { showError(getString(R.string.invalid_auth)) }
        )
        viewModel.startReAuthEventLiveData.observeData(this) {
            findNavController().navigate(ActiveSessionsDialogFragmentDirections.toReAuthStagesDialogFragment())
        }
    }
}