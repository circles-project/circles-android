package org.futo.circles.feature.sign_up.setup_circles

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.core.picker.MediaPickerHelper
import org.futo.circles.databinding.FragmentSetupCirclesBinding
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.observeResponse
import org.futo.circles.extensions.showSuccess
import org.futo.circles.feature.sign_up.setup_circles.list.SetupCirclesAdapter
import org.futo.circles.model.SetupCircleListItem
import org.koin.androidx.viewmodel.ext.android.viewModel

class SetupCirclesFragment : Fragment(R.layout.fragment_setup_circles), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModel<SetupCirclesViewModel>()
    private val binding by viewBinding(FragmentSetupCirclesBinding::bind)
    private val listAdapter by lazy { SetupCirclesAdapter(::onCircleListItemClicked) }
    private val mediaPickerHelper = MediaPickerHelper(this)


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
                startLoading(btnSave)
                viewModel.createCircles()
            }
        }
    }

    private fun setupObservers() {
        viewModel.circlesLiveData.observeData(this, ::setCirclesList)
        viewModel.createCirclesResponseLiveData.observeResponse(this,
            success = {
                showSuccess(getString(R.string.circles_created), true)
                navigateToBottomMenuScreen()
            })
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

    private fun navigateToBottomMenuScreen() {
        findNavController().navigate(SetupCirclesFragmentDirections.toBottomNavigationFragment())
    }
}