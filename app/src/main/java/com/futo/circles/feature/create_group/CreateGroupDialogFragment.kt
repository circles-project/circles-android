package com.futo.circles.feature.create_group

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.futo.circles.R
import com.futo.circles.core.fragment.BaseFullscreenDialogFragment
import com.futo.circles.core.fragment.HasLoadingState
import com.futo.circles.core.ImagePickerHelper
import com.futo.circles.databinding.CreateGroupDialogFragmentBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.extensions.observeResponse
import com.futo.circles.extensions.showSuccess
import com.futo.circles.feature.select_users.SelectUsersFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateGroupDialogFragment :
    BaseFullscreenDialogFragment(CreateGroupDialogFragmentBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModel<CreateGroupViewModel>()
    private val imagePickerHelper = ImagePickerHelper(this)

    private val binding by lazy {
        getBinding() as CreateGroupDialogFragmentBinding
    }

    private val selectedUsersFragment by lazy { SelectUsersFragment.create(null) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addSelectUsersFragment()
        setupViews()
        setupObservers()
    }

    private fun addSelectUsersFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.lContainer, selectedUsersFragment)
            .commitAllowingStateLoss()
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
            ivGroup.setOnClickListener {
                imagePickerHelper.showImagePickerDialog(onImageSelected = { _, uri ->
                    viewModel.setImageUri(uri)
                })
            }
            tilGroupName.editText?.doAfterTextChanged {
                it?.let { btnCreate.isEnabled = it.isNotEmpty() }
            }
            btnCreate.setOnClickListener {
                viewModel.createGroup(
                    tilGroupName.editText?.text?.toString()?.trim() ?: "",
                    tilGroupTopic.editText?.text?.toString()?.trim() ?: "",
                    selectedUsersFragment.getSelectedUsers()
                )
                startLoading(btnCreate)
            }
        }
    }

    private fun setupObservers() {
        viewModel.selectedImageLiveData.observeData(this) {
            binding.ivGroup.setImageURI(it)
        }
        viewModel.createGroupResponseLiveData.observeResponse(this,
            success = {
                showSuccess(getString(R.string.group_created), true)
                activity?.onBackPressed()
            }
        )
    }
}