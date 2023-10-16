package org.futo.circles.feature.timeline.post.info

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.databinding.DialogFragmentDebugInfoBinding
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment

@AndroidEntryPoint
class PostInfoDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentDebugInfoBinding::inflate) {

    private val args: PostInfoDialogFragmentArgs by navArgs()
    private val binding by lazy {
        getBinding() as DialogFragmentDebugInfoBinding
    }
    private val viewModel by viewModels<PostInfoViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.title = getString(R.string.info)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.infoLiveData.observeData(this) {
            binding.tvInfo.text = it
        }
    }
}