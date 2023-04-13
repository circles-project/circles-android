package org.futo.circles.feature.notifications.settings

import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.futo.circles.R
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentPushNotificationsSettingsBinding
import org.futo.circles.extensions.openNotificationSettings
import org.koin.androidx.viewmodel.ext.android.viewModel

class PushNotificationsSettingsDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentPushNotificationsSettingsBinding::inflate) {

    private val binding by lazy { getBinding() as DialogFragmentPushNotificationsSettingsBinding }
    private val viewModel by viewModel<PushNotificationsSettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
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
                    .navigate(PushNotificationsSettingsDialogFragmentDirections.toNotificationTestDialogFragment())
            }
            lNotificationsMethod.setOnClickListener { showSelectDistributorDialog() }
            setCurrentDistributorName()
        }
    }

    private fun showSelectDistributorDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.unifiedpush_distributors_dialog_title)
            .setPositiveButton(R.string.save) { dialogInterface, _ ->
                viewModel.saveSelectedDistributor()
                setCurrentDistributorName()
                dialogInterface.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .setCancelable(false)
            .setSingleChoiceItems(
                viewModel.getAvailableDistributors().toTypedArray(),
                0
            ) { _, index -> viewModel.onDistributorSelected(index) }
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