package org.futo.circles.core.feature.timeline.state

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.databinding.DialogFragmentDebugInfoBinding
import org.futo.circles.core.extensions.observeData

@AndroidEntryPoint
class RoomStateEventsDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentDebugInfoBinding>(DialogFragmentDebugInfoBinding::inflate) {

    private val viewModel by viewModels<RoomStateEventsViewModel>()

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