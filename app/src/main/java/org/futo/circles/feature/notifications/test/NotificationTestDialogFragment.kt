package org.futo.circles.feature.notifications.test

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import org.futo.circles.R
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentNotificationsTestBinding
import org.futo.circles.extensions.observeData
import org.futo.circles.feature.notifications.NotificationActionIds
import org.futo.circles.feature.notifications.test.list.NotificationsTestAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.matrix.android.sdk.api.extensions.tryOrNull

class NotificationTestDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentNotificationsTestBinding::inflate) {

    private val viewModel by viewModel<NotificationTestViewModel>()

    private val binding by lazy {
        getBinding() as DialogFragmentNotificationsTestBinding
    }

    private val testAdapter by lazy {
        NotificationsTestAdapter {
            viewModel.onFixNotificationTest(it)
        }
    }

    private val broadcastReceiverPush = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            viewModel.onTestPushReceived()
        }
    }

    private val broadcastReceiverNotification = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            viewModel.onTestPushClicked()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        tryOrNull(getString(R.string.unable_to_register_receiver)) {
            LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(broadcastReceiverPush, IntentFilter(NotificationActionIds.push))
        }
        tryOrNull(getString(R.string.unable_to_register_receiver)) {
            LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(
                    broadcastReceiverNotification,
                    IntentFilter(NotificationActionIds.diagnostic)
                )
        }
    }

    override fun onPause() {
        super.onPause()
        tryOrNull {
            LocalBroadcastManager.getInstance(requireContext())
                .unregisterReceiver(broadcastReceiverPush)
        }
        tryOrNull {
            LocalBroadcastManager.getInstance(requireContext())
                .unregisterReceiver(broadcastReceiverNotification)
        }
    }

    private fun setupViews() {
        binding.rvNotificationsTests.adapter = testAdapter
    }

    private fun setupObservers() {
        viewModel.testsLiveData.observeData(this) {
            testAdapter.submitList(it)
        }
    }
}