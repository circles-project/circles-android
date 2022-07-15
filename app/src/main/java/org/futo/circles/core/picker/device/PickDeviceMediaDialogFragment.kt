package org.futo.circles.core.picker.device

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.list.BaseRvDecoration
import org.futo.circles.core.picker.MediaPickerHelper.Companion.IS_VIDEO_AVAILABLE
import org.futo.circles.core.picker.device.list.DeviceMediaViewHolder
import org.futo.circles.core.picker.device.list.DeviceMedialListAdapter
import org.futo.circles.databinding.PickDeviceMediaDialogFragmentBinding
import org.futo.circles.extensions.observeData
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PickDeviceMediaDialogFragment :
    BaseFullscreenDialogFragment(PickDeviceMediaDialogFragmentBinding::inflate) {

    private val binding by lazy {
        getBinding() as PickDeviceMediaDialogFragmentBinding
    }

    private val viewModel by viewModel<PickDeviceMediaViewModel> {
        parametersOf(arguments?.getBoolean(IS_VIDEO_AVAILABLE) ?: false)
    }

    private val listAdapter by lazy {
        DeviceMedialListAdapter(onClick = { postId -> })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.rvMedia.apply {
            adapter = listAdapter
            addItemDecoration(BaseRvDecoration.OffsetDecoration<DeviceMediaViewHolder>(2))
        }
    }

    private fun setupObservers() {
        viewModel.mediaLiveData.observeData(this) { listAdapter.submitList(it) }
    }

    companion object {
        fun create(isVideoAvailable: Boolean) = PickDeviceMediaDialogFragment().apply {
            arguments = bundleOf(IS_VIDEO_AVAILABLE to isVideoAvailable)
        }
    }
}