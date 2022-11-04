package org.futo.circles.feature.room.create_room

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import org.futo.circles.R
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.core.picker.MediaPickerHelper
import org.futo.circles.databinding.DialogFragmentCreateRoomBinding
import org.futo.circles.extensions.*
import org.futo.circles.feature.room.select_users.SelectUsersFragment
import org.futo.circles.model.CircleRoomTypeArg
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateRoomDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentCreateRoomBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModel<CreateRoomViewModel>()
    private val mediaPickerHelper = MediaPickerHelper(this)
    private val args: CreateRoomDialogFragmentArgs by navArgs()

    private val binding by lazy {
        getBinding() as DialogFragmentCreateRoomBinding
    }

    private var selectedUsersFragment: SelectUsersFragment? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }


    private fun setupViews() {
        setupInviteMembers()
        with(binding) {
            toolbar.title = getTitle()
            tvNameHeader.text = getCreateHeader()
            topicViewGroup.setIsVisible(args.type == CircleRoomTypeArg.Group)
            ivCover.setOnClickListener {
                mediaPickerHelper.showMediaPickerDialog(onImageSelected = { _, uri ->
                    viewModel.setImageUri(uri)
                })
            }
            tilName.editText?.doAfterTextChanged {
                it?.let { btnCreate.isEnabled = it.isNotEmpty() }
            }
            btnCreate.setOnClickListener {
                viewModel.createRoom(
                    tilName.getText(),
                    tilTopic.getText(),
                    selectedUsersFragment?.getSelectedUsersIds(),
                    args.type
                )
                startLoading(btnCreate)
            }
        }
    }

    private fun setupInviteMembers() {
        val isInvitesAvailable = args.type != CircleRoomTypeArg.Photo
        binding.tvInviteUsers.setIsVisible(isInvitesAvailable)
        if (isInvitesAvailable) addSelectUsersFragment()
    }

    private fun setupObservers() {
        viewModel.selectedImageLiveData.observeData(this) {
            binding.ivCover.setImageURI(it)
        }
        viewModel.createRoomResponseLiveData.observeResponse(this,
            success = { onBackPressed() }
        )
    }

    private fun getTitle() = context?.getString(
        when (args.type) {
            CircleRoomTypeArg.Circle -> R.string.create_new_circle
            CircleRoomTypeArg.Group -> R.string.create_new_group
            CircleRoomTypeArg.Photo -> R.string.create_new_gallery
        }
    )

    private fun getCreateHeader() = context?.getString(
        when (args.type) {
            CircleRoomTypeArg.Circle -> R.string.circle_name
            CircleRoomTypeArg.Group -> R.string.group_name
            CircleRoomTypeArg.Photo -> R.string.gallery_name
        }
    )

    private fun addSelectUsersFragment() {
        selectedUsersFragment = SelectUsersFragment.create(null).also {
            childFragmentManager.beginTransaction()
                .replace(R.id.lContainer, it)
                .commitAllowingStateLoss()
        }
    }
}