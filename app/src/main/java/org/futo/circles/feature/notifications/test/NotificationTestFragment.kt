package org.futo.circles.feature.notifications.test

import android.os.Bundle
import android.view.View
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.FragmentNotificationsTestBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationTestFragment :
    BaseFullscreenDialogFragment(FragmentNotificationsTestBinding::inflate) {

    private val viewModel by viewModel<NotificationTestViewModel>()

    private val binding by lazy {
        getBinding() as FragmentNotificationsTestBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}