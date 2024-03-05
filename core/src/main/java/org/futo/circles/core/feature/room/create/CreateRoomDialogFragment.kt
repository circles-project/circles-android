package org.futo.circles.core.feature.room.create

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.os.BundleCompat.getParcelable
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.databinding.DialogFragmentCreateRoomBinding
import org.futo.circles.core.extensions.getRoleNameResId
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.feature.picker.helper.MediaPickerHelper
import org.futo.circles.core.feature.select_users.SelectUsersFragment
import org.futo.circles.core.model.AccessLevel
import org.futo.circles.core.model.CircleRoomTypeArg
import org.matrix.android.sdk.api.session.room.powerlevels.Role


@AndroidEntryPoint
class CreateRoomDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentCreateRoomBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModels<CreateRoomViewModel>()
    private val binding by lazy {
        getBinding() as DialogFragmentCreateRoomBinding
    }

    private val mediaPickerHelper = MediaPickerHelper(this)
    private val roomType: CircleRoomTypeArg by lazy {
        getParcelable(requireArguments(), "type", CircleRoomTypeArg::class.java)
            ?: CircleRoomTypeArg.Group
    }
    private var selectedUsersFragment: SelectUsersFragment? = null

    private val circleTypeList = AccessLevel.entries.toTypedArray()
    private val circleTypeAdapter by lazy {
        ArrayAdapter(
            requireContext(),
            R.layout.view_spinner_item,
            circleTypeList.map {
                val role = Role.fromValue(it.levelValue, Role.Default.value)
                getString(role.getRoleNameResId())
            }).apply {
            setDropDownViewResource(R.layout.view_spinner_item)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) addSelectUsersFragment()
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            val roomTypeName = getRoomTypeName()
            toolbar.title = getString(R.string.create_new_room_format, roomTypeName)
            val nameHeader = "$roomTypeName ${getString(R.string.name)}"
            tvNameHeader.text = nameHeader
            val isGroup = roomType == CircleRoomTypeArg.Group
            tvTopicHeader.setIsVisible(isGroup)
            tilTopic.setIsVisible(isGroup)
            val isCircle = roomType == CircleRoomTypeArg.Circle
            tvTypeHeader.setIsVisible(isCircle)
            circleTypeGroup.setIsVisible(isCircle)
            lCircleTypeExplanation.setIsVisible(isCircle)
            ivCover.setOnClickListener { changeCoverImage() }
            tilName.editText?.doAfterTextChanged {
                it?.let { btnCreate.isEnabled = it.isNotEmpty() }
            }
            btnCreate.setOnClickListener {
                createRoom()
                startLoading(btnCreate)
            }
            spUserRole.apply {
                adapter = circleTypeAdapter
                setSelection(AccessLevel.User.ordinal)
            }
        }
    }

    private fun setupObservers() {
        viewModel.selectedImageLiveData.observeData(this) {
            binding.ivCover.setImageURI(it)
        }
        viewModel.createRoomResponseLiveData.observeResponse(this,
            success = { onBackPressed() }
        )
    }

    private fun getRoomTypeName() = getString(
        when (roomType) {
            CircleRoomTypeArg.Circle -> R.string.circle
            CircleRoomTypeArg.Group -> R.string.group
            CircleRoomTypeArg.Photo -> R.string.gallery
        }
    )

    private fun changeCoverImage() {
        mediaPickerHelper.showMediaPickerDialog(onImageSelected = { _, uri ->
            viewModel.setImageUri(uri)
        })
    }

    private fun createRoom() {
        viewModel.createRoom(
            binding.tilName.getText(),
            binding.tilTopic.getText(),
            selectedUsersFragment?.getSelectedUsersIds(),
            roomType,
            binding.btnPublic.isChecked,
            AccessLevel.entries.getOrNull(binding.spUserRole.selectedItemPosition)
                ?: AccessLevel.User
        )
    }

    private fun addSelectUsersFragment() {
        selectedUsersFragment = SelectUsersFragment.create(null).also {
            childFragmentManager.beginTransaction()
                .replace(R.id.lContainer, it)
                .commitAllowingStateLoss()
        }
    }
}