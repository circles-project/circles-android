package org.futo.circles.core.timeline.state

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentDebugInfoBinding

@AndroidEntryPoint
class RoomStateEventsDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentDebugInfoBinding::inflate) {

    private val args: RoomStateEventsDialogFragmentArgs by navArgs()
    private val binding by lazy {
        getBinding() as DialogFragmentDebugInfoBinding
    }
    private val viewModel by viewModels<RoomStateEventsViewModel>()
//    {
//        parametersOf(args.roomId)
//    }

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