package org.futo.circles.feature.settings.active_sessions

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.ActiveSessionsDialogFragmentBinding
import org.futo.circles.extensions.observeData
import org.futo.circles.feature.settings.active_sessions.list.ActiveSessionClickListener
import org.futo.circles.feature.settings.active_sessions.list.ActiveSessionsAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class ActiveSessionsDialogFragment :
    BaseFullscreenDialogFragment(ActiveSessionsDialogFragmentBinding::inflate) {

    private val viewModel by viewModel<ActiveSessionsViewModel>()

    private val binding by lazy {
        getBinding() as ActiveSessionsDialogFragmentBinding
    }

    private val sessionsListAdapter by lazy {
        ActiveSessionsAdapter(object : ActiveSessionClickListener {
            override fun onItemClicked(deviceId: String) {
                viewModel.onSessionClicked(deviceId)
            }

            override fun onVerifySessionClicked(deviceId: String) {
                viewModel.verifySession(deviceId)
            }

            override fun onRemoveSessionClicked(deviceId: String) {
                findNavController().navigate(
                    ActiveSessionsDialogFragmentDirections.toRemoveSessionDialogFragment(deviceId)
                )
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
    }
}