package com.futo.circles.feature.settings.change_password

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.futo.circles.core.fragment.BaseFullscreenDialogFragment
import com.futo.circles.core.fragment.HasLoadingState
import com.futo.circles.databinding.ChangePasswordDialogFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChangePasswordDialogFragment :
    BaseFullscreenDialogFragment(ChangePasswordDialogFragmentBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModel<ChangePasswordViewModel>()

    private val binding by lazy {
        getBinding() as ChangePasswordDialogFragmentBinding
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