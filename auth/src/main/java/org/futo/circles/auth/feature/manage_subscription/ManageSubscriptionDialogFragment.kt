package org.futo.circles.auth.feature.manage_subscription

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.databinding.DialogFragmentManageSubscriptionBinding
import org.futo.circles.auth.subscriptions.SubscriptionProvider
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import javax.inject.Inject

@AndroidEntryPoint
class ManageSubscriptionDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentManageSubscriptionBinding::inflate) {

    private val viewModel by viewModels<ManageSubscriptionViewModel>()

    private val binding by lazy {
        getBinding() as DialogFragmentManageSubscriptionBinding
    }

    @Inject
    lateinit var subscriptionProvider: SubscriptionProvider

    private val subscriptionManager by lazy {
        subscriptionProvider.getManager(this, null)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getSubscriptionInfo(subscriptionManager)
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