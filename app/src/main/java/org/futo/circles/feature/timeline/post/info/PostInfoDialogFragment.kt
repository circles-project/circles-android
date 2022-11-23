package org.futo.circles.feature.timeline.post.info

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentPostInfoBinding
import org.futo.circles.extensions.observeData
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PostInfoDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentPostInfoBinding::inflate) {

    private val args: PostInfoDialogFragmentArgs by navArgs()
    private val binding by lazy {
        getBinding() as DialogFragmentPostInfoBinding
    }
    private val viewModel by viewModel<PostInfoViewModel> {
        parametersOf(args.roomId, args.eventId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.infoLiveData.observeData(this) {
            binding.tvInfo.text = it
        }
    }
}