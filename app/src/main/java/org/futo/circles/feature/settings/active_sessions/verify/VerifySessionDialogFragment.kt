package org.futo.circles.feature.settings.active_sessions.verify

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.navArgs
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentActiveSessionsBinding
import org.futo.circles.databinding.DialogFragmentVerifySessionBinding
import org.futo.circles.feature.notices.SystemNoticesDialogFragmentArgs
import org.futo.circles.feature.notices.SystemNoticesTimelineViewModel
import org.futo.circles.feature.settings.active_sessions.ActiveSessionsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class VerifySessionDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentVerifySessionBinding::inflate) {

    private val args: VerifySessionDialogFragmentArgs by navArgs()

    private val viewModel by viewModel<VerifySessionViewModel> {
        parametersOf(args.deviceId)
    }

    private val binding by lazy {
        getBinding() as DialogFragmentVerifySessionBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MyLog", "fragment")
        viewModel.requestVerification()
    }
}