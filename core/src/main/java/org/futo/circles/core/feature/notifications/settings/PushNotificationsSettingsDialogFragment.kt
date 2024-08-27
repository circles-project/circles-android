package org.futo.circles.core.feature.notifications.settings

import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.databinding.DialogFragmentPushNotificationsSettingsBinding
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.openNotificationSettings

@AndroidEntryPoint
class PushNotificationsSettingsDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentPushNotificationsSettingsBinding>(
        DialogFragmentPushNotificationsSettingsBinding::inflate
    ) {

    private val viewModel by viewModels<PushNotificationsSettingsViewModel>()
    private var selectedDistributorIndex = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        updatePushNotificationStatus()
    }

    private fun setupViews() {
        with(binding) {
            lPushNotifications.setOnClickListener { openNotificationSettings() }
            tvNotificationsTest.setOnClickListener {
                findNavController()
                    .navigateSafe(PushNotificationsSettingsDialogFragmentDirections.toNotificationTestDialogFragment())
            }
            lNotificationsMethod.setOnClickListener { showSelectDistributorDialog() }
            setCurrentDistributorName()
        }
    }

    private fun setupObservers() {
        viewModel.pushDistributorChangedEventLiveData.observeData(this) {
            setCurrentDistributorName()
        }
    }

    private fun showSelectDistributorDialog() {
        selectedDistributorIndex = viewModel.getSavedDistributorIndex()
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.unifiedpush_distributors_dialog_title)
            .setPositiveButton(R.string.save) { dialogInterface, _ ->
                viewModel.saveSelectedDistributor(selectedDistributorIndex)
                dialogInterface.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .setCancelable(false)
            .setSingleChoiceItems(
                viewModel.getAvailableDistributorsNames().toTypedArray(),
                selectedDistributorIndex
            ) { _, index -> selectedDistributorIndex = index }
            .show()
    }

    private fun updatePushNotificationStatus() {
        val isPushNotificationsAllowed =
            NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
        with(binding) {
            svPushNotificationsStatus.isChecked = isPushNotificationsAllowed
            tvNotificationsTest.isEnabled = isPushNotificationsAllowed
            lNotificationsMethod.isEnabled = isPushNotificationsAllowed
            tvNotificationsMethod.isEnabled = isPushNotificationsAllowed
        }
    }

    private fun setCurrentDistributorName() {
        binding.tvSelectedNotificationsMethod.text = viewModel.getCurrentDistributorName()
    }

}