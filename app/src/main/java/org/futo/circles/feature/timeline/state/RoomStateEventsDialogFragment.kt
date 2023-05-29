package org.futo.circles.feature.timeline.state

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import org.futo.circles.R
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentDebugInfoBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RoomStateEventsDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentDebugInfoBinding::inflate) {

    private val args: RoomStateEventsDialogFragmentArgs by navArgs()
    private val binding by lazy {
        getBinding() as DialogFragmentDebugInfoBinding
    }
    private val viewModel by viewModel<RoomStateEventsViewModel> {
        parametersOf(args.roomId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.title = getString(R.string.state_events)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.stateEventsLiveData?.observeData(this) {
            binding.tvInfo.text = it
        }
    }
}