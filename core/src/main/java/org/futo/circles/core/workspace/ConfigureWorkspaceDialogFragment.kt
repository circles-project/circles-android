package org.futo.circles.core.workspace

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.databinding.DialogFragmentConfigureWorkspaceBinding
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment

@AndroidEntryPoint
class ConfigureWorkspaceDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentConfigureWorkspaceBinding::inflate) {

    private val viewModel by viewModels<ConfigureWorkspaceViewModel>()

    private val binding by lazy {
        getBinding() as DialogFragmentConfigureWorkspaceBinding
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {

    }

    private fun setupObservers() {

    }


}
