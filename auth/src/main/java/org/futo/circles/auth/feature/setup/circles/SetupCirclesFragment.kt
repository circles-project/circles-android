package org.futo.circles.auth.feature.setup.circles

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.FragmentSetupCirclesBinding
import org.futo.circles.auth.feature.setup.circles.dialog.AddSetupCirclesItemDialog
import org.futo.circles.auth.feature.setup.circles.list.SetupCirclesAdapter
import org.futo.circles.core.base.fragment.BaseBindingFragment
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.feature.picker.helper.MediaPickerHelper
import org.futo.circles.core.view.LoadingDialog

@AndroidEntryPoint
class SetupCirclesFragment :
    BaseBindingFragment<FragmentSetupCirclesBinding>(FragmentSetupCirclesBinding::inflate),
    HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModels<SetupCirclesViewModel>()
    private val mediaPickerHelper = MediaPickerHelper(this)
    private val workspaceLoadingDialog by lazy { LoadingDialog(requireContext()) }
    private val listAdapter by lazy {
        SetupCirclesAdapter(
            onChangeImage = { id ->
                mediaPickerHelper.showMediaPickerDialog(onImageSelected = { _, uri ->
                    viewModel.setImageUriForCircle(id, uri)
                })
            },
            onRemove = { id ->
                viewModel.removeCircle(id)
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), org.futo.circles.core.R.color.default_background)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            rvCircles.adapter = listAdapter
            btnAdd.setOnClickListener {
                AddSetupCirclesItemDialog(requireContext()) { name ->
                    viewModel.addCircleItem(name)
                }.show()
            }
            btnNext.setOnClickListener {
                startLoading(btnNext)
                viewModel.createWorkspace()
            }
        }
    }

    private fun setupObservers() {
        viewModel.circlesLiveData.observeData(this) {
            listAdapter.submitList(it)
        }
        viewModel.workspaceResultLiveData.observeResponse(
            this,
            success = {
                findNavController().navigateSafe(SetupCirclesFragmentDirections.toSetupProfileFragment())
            },
            error = {
                showError(it)
                binding.btnNext.setText(getString(R.string.retry))
            }
        )
        viewModel.workspaceLoadingLiveData.observeData(this) {
            workspaceLoadingDialog.handleLoading(it)
        }
    }
}