package org.futo.circles.feature.notifications.test

import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.databinding.FragmentNotificationsTestBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationTestFragment : Fragment(R.layout.fragment_notifications_test) {

    private val viewModel by viewModel<NotificationTestViewModel>()
    private val binding by viewBinding(FragmentNotificationsTestBinding::bind)
}