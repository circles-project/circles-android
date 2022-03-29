package com.futo.circles.feature.setup_circles

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.core.HasLoadingState
import com.futo.circles.core.ImagePickerHelper
import com.futo.circles.databinding.SetupCirclesFragmentBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.feature.setup_circles.list.SetupCirclesAdapter
import com.futo.circles.model.SetupCircleListItem
import org.koin.androidx.viewmodel.ext.android.viewModel

class SetupCirclesFragment : Fragment(R.layout.setup_circles_fragment), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModel<SetupCirclesViewModel>()
    private val binding by viewBinding(SetupCirclesFragmentBinding::bind)
    private val listAdapter by lazy { SetupCirclesAdapter(::onCircleListItemClicked) }
    private val imagePickerHelper by lazy { ImagePickerHelper(this) }


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
            btnSave.setOnClickListener { viewModel.createCircles() }
        }
    }

    private fun setupObservers() {
        viewModel.circlesLiveData.observeData(this, ::setCirclesList)
    }

    private fun setCirclesList(list: List<SetupCircleListItem>) {
        listAdapter.submitList(list)
    }

    private fun onCircleListItemClicked(circle: SetupCircleListItem) {
        imagePickerHelper.showImagePickerDialog(
            onImageSelected = { id, uri -> viewModel.addImageForCircle(id,uri)},
            id = circle.id
        )
    }

    private fun navigateToBottomMenuScreen() {
        findNavController().navigate(SetupCirclesFragmentDirections.toBottomNavigationFragment())
    }
}