package org.futo.circles.feature.notifications.test

import android.os.Bundle
import android.view.View
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentNotificationsTestBinding
import org.futo.circles.feature.notifications.test.list.NotificationsTestAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationTestDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentNotificationsTestBinding::inflate) {

    private val viewModel by viewModel<NotificationTestViewModel>()

    private val binding by lazy {
        getBinding() as DialogFragmentNotificationsTestBinding
    }

    private val testAdapter by lazy { NotificationsTestAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.rvNotificationsTests.adapter = testAdapter
    }

    private fun setupObservers() {

    }
}