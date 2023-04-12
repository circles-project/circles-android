package org.futo.circles.feature.notifications.settings

import androidx.core.app.NotificationManagerCompat
import org.futo.circles.R
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentPushNotificationsSettingsBinding

class PushNotificationsSettingsDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentPushNotificationsSettingsBinding::inflate) {

    private val binding by lazy { getBinding() as DialogFragmentPushNotificationsSettingsBinding }

    override fun onResume() {
        super.onResume()
        updatePushNotificationStatus()
    }

    private fun updatePushNotificationStatus() {
        val isPushNotificationsAllowed =
            NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
        binding.tvPushNotificationsStatus.text =
            getString(if (isPushNotificationsAllowed) R.string.enabled else R.string.disabled)
    }

}