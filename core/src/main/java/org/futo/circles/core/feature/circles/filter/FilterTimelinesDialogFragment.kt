package org.futo.circles.core.feature.circles.filter

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.databinding.DialogFragmentFilterTimelineBinding
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.feature.circles.filter.list.FilterTimelinesAdapter
import org.futo.circles.core.mapping.nameOrId


@AndroidEntryPoint
class FilterTimelinesDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentFilterTimelineBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModels<FilterTimelinesViewModel>()

    private val binding by lazy {
        getBinding() as DialogFragmentFilterTimelineBinding
    }

    private val filterTimelinesAdapter by lazy {
        FilterTimelinesAdapter(
            onItemSelected = { id ->
                binding.btnSave.isEnabled = true
                viewModel.toggleItemSelected(id)
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            btnSave.setOnClickListener {
                startLoading(btnSave)
                viewModel.saveFilter()
            }
            btnSelectAll.setOnClickListener {
                binding.btnSave.isEnabled = true
                viewModel.selectAllItems()
            }
            rvTimelines.apply {
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                adapter = filterTimelinesAdapter
            }
        }
    }

    private fun setupObservers() {
        viewModel.circleInfoLiveData.observeData(this) {
            it.getOrNull()?.let { info ->
                binding.tvSubtitle.text =
                    getString(R.string.select_timelines_format, info.nameOrId())
            }
        }
        viewModel.timelinesLiveData.observeData(this) {
            filterTimelinesAdapter.submitList(it)
        }
        viewModel.updateFilterResultLiveData.observeResponse(this,
            success = {
                showSuccess(getString(R.string.updated))
                dismiss()
            })
    }

}