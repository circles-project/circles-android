package org.futo.circles.feature.circles.setup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.core.model.LoadingData
import org.futo.circles.core.picker.helper.MediaPickerHelper
import org.futo.circles.core.view.LoadingDialog
import org.futo.circles.databinding.FragmentSetupCirclesBinding
import org.futo.circles.feature.circles.setup.list.SetupCirclesAdapter
import org.futo.circles.model.SetupCircleListItem

@AndroidEntryPoint
class SetupCirclesFragment : Fragment(R.layout.fragment_setup_circles), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModels<SetupCirclesViewModel>()
    private val binding by viewBinding(FragmentSetupCirclesBinding::bind)
    private val listAdapter by lazy { SetupCirclesAdapter(::onCircleListItemClicked) }
    private val mediaPickerHelper = MediaPickerHelper(this, isGalleryAvailable = false)
    private val loadingDialog by lazy { LoadingDialog(requireContext()) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            rvSetupCircles.apply {
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                adapter = listAdapter
            }
            btnSkip.setOnClickListener { navigateToBottomMenuScreen() }
            btnSave.setOnClickListener {
                showLoading()
                viewModel.createCircles()
            }
        }
    }

    private fun setupObservers() {
        viewModel.circlesLiveData.observeData(this, ::setCirclesList)
        viewModel.createCirclesResponseLiveData.observeResponse(this,
            success = {
                loadingDialog.dismiss()
                showSuccess(getString(R.string.circles_created))
                navigateToBottomMenuScreen()
            },
            error = {
                showError(it)
                loadingDialog.dismiss()
            }
        )
    }

    private fun setCirclesList(list: List<SetupCircleListItem>) {
        listAdapter.submitList(list)
    }

    private fun onCircleListItemClicked(circle: SetupCircleListItem) {
        mediaPickerHelper.showMediaPickerDialog(
            onImageSelected = { id, uri -> viewModel.addImageForCircle(id, uri) },
            id = circle.id
        )
    }

    private fun showLoading() {
        startLoading(binding.btnSave)
        loadingDialog.handleLoading(LoadingData(messageId = R.string.configuring_workspace))
    }

    private fun navigateToBottomMenuScreen() {
        findNavController().navigateSafe(SetupCirclesFragmentDirections.toHomeFragment())
    }
}