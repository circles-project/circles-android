package org.futo.circles.auth.feature.manage_subscription

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.databinding.DialogFragmentManageSubscriptionBinding
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.base.fragment.HasLoadingState

@AndroidEntryPoint
class ManageSubscriptionDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentManageSubscriptionBinding::inflate),
    HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModels<ManageSubscriptionViewModel>()

    private val binding by lazy {
        getBinding() as DialogFragmentManageSubscriptionBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {

    }
}