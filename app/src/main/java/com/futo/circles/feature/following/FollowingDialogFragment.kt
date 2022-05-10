package com.futo.circles.feature.following

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.futo.circles.core.fragment.BaseFullscreenDialogFragment
import com.futo.circles.databinding.FollowingDialogFragmentBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.feature.following.list.FollowingAdapter
import com.futo.circles.model.FollowingListItem
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FollowingDialogFragment :
    BaseFullscreenDialogFragment(FollowingDialogFragmentBinding::inflate) {

    private val args: FollowingDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<FollowingViewModel> {
        parametersOf(args.roomId)
    }
    private val binding by lazy {
        getBinding() as FollowingDialogFragmentBinding
    }
    private val listAdapter by lazy {
        FollowingAdapter(onRemoveClicked = { showRemoveOptionsDialog(it) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.rvRooms.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = listAdapter
        }
    }

    private fun setupObservers() {
        viewModel.roomsLiveData.observeData(this) { listAdapter.submitList(it) }
    }

    private fun showRemoveOptionsDialog(item: FollowingListItem) {

    }
}