package org.futo.circles.core.feature.circles.filter

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.databinding.DialogFragmentFilterTimelineBinding
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.mapping.nameOrId


@AndroidEntryPoint
class FilterTimelinesDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentFilterTimelineBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModels<FilterTimelinesViewModel>()

    private val binding by lazy {
        getBinding() as DialogFragmentFilterTimelineBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {

    }

    private fun setupObservers() {
        viewModel.circleInfoLiveData.observeData(this) {
            it.getOrNull()?.let { info ->
                binding.tvSubtitle.text =
                    getString(R.string.select_timelines_format, info.nameOrId())
            }
        }
    }

}