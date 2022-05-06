package com.futo.circles.feature.create_room

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.futo.circles.R
import com.futo.circles.core.ImagePickerHelper
import com.futo.circles.core.fragment.BaseFullscreenDialogFragment
import com.futo.circles.core.fragment.HasLoadingState
import com.futo.circles.model.CircleRoomTypeArg
import com.futo.circles.databinding.CreateRoomDialogFragmentBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.extensions.observeResponse
import com.futo.circles.extensions.setIsVisible
import com.futo.circles.extensions.showSuccess
import com.futo.circles.feature.select_users.SelectUsersFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateRoomDialogFragment :
    BaseFullscreenDialogFragment(CreateRoomDialogFragmentBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModel<CreateRoomViewModel>()
    private val imagePickerHelper = ImagePickerHelper(this)
    private val args: CreateRoomDialogFragmentArgs by navArgs()
    private val isCreateGroup by lazy { args.type == CircleRoomTypeArg.Group }

    private val binding by lazy {
        getBinding() as CreateRoomDialogFragmentBinding
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
            toolbar.title =
                context?.getString(if (isCreateGroup) R.string.create_new_group else R.string.create_new_circle)
            tvNameHeader.text =
                context?.getString(if (isCreateGroup) R.string.group_name else R.string.circle_name)
            topicViewGroup.setIsVisible(isCreateGroup)
            ivCover.setOnClickListener {
                imagePickerHelper.showImagePickerDialog(onImageSelected = { _, uri ->
                    viewModel.setImageUri(uri)
                })
            }
            tilName.editText?.doAfterTextChanged {
                it?.let { btnCreate.isEnabled = it.isNotEmpty() }
            }
            btnCreate.setOnClickListener {
                viewModel.createRoom(
                    tilName.editText?.text?.toString()?.trim() ?: "",
                    tilTopic.editText?.text?.toString()?.trim() ?: "",
                    selectedUsersFragment.getSelectedUsers(),
                    isCreateGroup
                )
                startLoading(btnCreate)
            }
        }
    }

    private fun setupObservers() {
        viewModel.selectedImageLiveData.observeData(this) {
            binding.ivCover.setImageURI(it)
        }
        viewModel.createRoomResponseLiveData.observeResponse(this,
            success = {
                showSuccess(
                    getString(if (isCreateGroup) R.string.group_created else R.string.circle_created),
                    true
                )
                activity?.onBackPressed()
            }
        )
    }
}