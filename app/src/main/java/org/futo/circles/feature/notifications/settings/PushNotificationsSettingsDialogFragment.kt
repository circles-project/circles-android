package org.futo.circles.feature.notifications.settings

import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.fragment.findNavController
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentPushNotificationsSettingsBinding
import org.futo.circles.extensions.openNotificationSettings

class PushNotificationsSettingsDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentPushNotificationsSettingsBinding::inflate) {

    private val binding by lazy { getBinding() as DialogFragmentPushNotificationsSettingsBinding }

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
        }
    }

    private fun updatePushNotificationStatus() {
        val isPushNotificationsAllowed =
            NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
        binding.svPushNotificationsStatus.isChecked = isPushNotificationsAllowed
    }

}