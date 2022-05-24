package com.futo.circles.feature.settings.active_sessions

import android.os.Bundle
import android.view.View
import com.futo.circles.core.fragment.BaseFullscreenDialogFragment
import com.futo.circles.databinding.ActiveSessionsDialogFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ActiveSessionsDialogFragment :
    BaseFullscreenDialogFragment(ActiveSessionsDialogFragmentBinding::inflate) {

    private val viewModel by viewModel<ActiveSessionsViewModel>()

    private val binding by lazy {
        getBinding() as ActiveSessionsDialogFragmentBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        }
    }

    private fun setupObservers() {

    }
}