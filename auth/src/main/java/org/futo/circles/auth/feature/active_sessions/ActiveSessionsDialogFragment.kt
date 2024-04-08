package org.futo.circles.auth.feature.active_sessions

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.DialogFragmentActiveSessionsBinding
import org.futo.circles.auth.feature.active_sessions.list.ActiveSessionClickListener
import org.futo.circles.auth.feature.active_sessions.list.ActiveSessionsAdapter
import org.futo.circles.auth.model.RemoveSession
import org.futo.circles.auth.model.ResetKeys
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment

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
                findNavController().navigateSafe(
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
            error = { showError(getString(R.string.the_password_you_entered_is_incorrect)) }
        )
        viewModel.resetKeysLiveData.observeResponse(this,
            error = { showError(getString(R.string.the_password_you_entered_is_incorrect)) }
        )
        viewModel.startReAuthEventLiveData.observeData(this) {
            findNavController().navigateSafe(ActiveSessionsDialogFragmentDirections.toUiaDialogFragment())
        }
    }
}