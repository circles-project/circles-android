package com.futo.circles.feature.settings.active_sessions

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import com.futo.circles.core.fragment.BaseFullscreenDialogFragment
import com.futo.circles.databinding.ActiveSessionsDialogFragmentBinding
import com.futo.circles.feature.settings.active_sessions.list.ActiveSessionsAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class ActiveSessionsDialogFragment :
    BaseFullscreenDialogFragment(ActiveSessionsDialogFragmentBinding::inflate) {

    private val viewModel by viewModel<ActiveSessionsViewModel>()

    private val binding by lazy {
        getBinding() as ActiveSessionsDialogFragmentBinding
    }

    private val sessionsListAdapter by lazy {
        ActiveSessionsAdapter()
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

    }
}