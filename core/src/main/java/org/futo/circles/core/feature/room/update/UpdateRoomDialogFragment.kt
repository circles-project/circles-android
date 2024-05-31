package org.futo.circles.core.feature.room.update

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.databinding.DialogFragmentUpdateRoomBinding
import org.futo.circles.core.extensions.getCircleAvatarUrl
import org.futo.circles.core.extensions.getRoleNameResId
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.loadRoomProfileIcon
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.feature.picker.helper.MediaPickerHelper
import org.futo.circles.core.model.AccessLevel
import org.futo.circles.core.model.CircleRoomTypeArg
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.model.usersDefaultOrDefault
import org.matrix.android.sdk.api.session.room.powerlevels.Role

@AndroidEntryPoint
class UpdateRoomDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentUpdateRoomBinding>(DialogFragmentUpdateRoomBinding::inflate),
    HasLoadingState {

    override val fragment: Fragment = this
    private val args: UpdateRoomDialogFragmentArgs by navArgs()
    private val viewModel by viewModels<UpdateRoomViewModel>()
    private val roomId: String get() = args.roomId
    private val roomType: CircleRoomTypeArg get() = args.type
    private val mediaPickerHelper = MediaPickerHelper(this)

    private val circleTypeList = AccessLevel.entries.toTypedArray()
    private val circleTypeAdapter by lazy {
        ArrayAdapter(requireContext(), R.layout.view_spinner_item, circleTypeList.map {
            val role = Role.fromValue(it.levelValue, Role.Default.value)
            getString(role.getRoleNameResId())
        }).apply {
            setDropDownViewResource(R.layout.view_spinner_item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            val roomTypeName = getRoomTypeName()
            toolbar.title = getString(R.string.configure_room_format, roomTypeName)
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
            btnChangeIcon.setOnClickListener { changeCoverImage() }
            tilName.editText?.doAfterTextChanged {
                it?.let { onInputDataChanged() }
            }
            binding.circleTypeGroup.setOnCheckedChangeListener { _, _ ->
                onInputDataChanged()
            }
            tilTopic.editText?.doAfterTextChanged {
                it?.let { onInputDataChanged() }
            }
            btnSave.setOnClickListener {
                viewModel.update(
                    tilName.getText(),
                    tilTopic.getText(),
                    binding.btnPublic.isChecked,
                    getSelectedAccessLevel(),
                    roomType
                )
                startLoading(btnSave)
            }
            tvRoleHeader.setIsVisible(!isCircle)
            spUserRole.apply {
                setIsVisible(!isCircle)
                adapter = circleTypeAdapter
                onItemSelectedListener = object : OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        onInputDataChanged()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }

                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.selectedImageLiveData.observeData(this) {
            binding.ivCover.setImageURI(it)
            onInputDataChanged()
        }
        viewModel.updateRoomResponseLiveData.observeResponse(this, success = {
            showSuccess(getString(R.string.updated))
            onBackPressed()
        })
        viewModel.roomSummaryLiveData.observeData(this) {
            it?.let { setInitialRoomData(it) }
        }
        viewModel.roomPowerLevelLiveData.observeData(this) {
            val userDefaultRoleLevel = it.usersDefaultOrDefault()
            binding.spUserRole.setSelection(AccessLevel.fromValue(userDefaultRoleLevel).ordinal)
        }
        viewModel.isRoomDataChangedLiveData.observeData(this) {
            binding.btnSave.isEnabled = it
        }
    }

    private fun setInitialRoomData(room: RoomSummary) {
        with(binding) {
            ivCover.loadRoomProfileIcon(
                if (roomType == CircleRoomTypeArg.Circle) room.getCircleAvatarUrl() else room.avatarUrl,
                room.displayName
            )
            tilName.editText?.setText(room.displayName)
            tilTopic.editText?.setText(room.topic)
            val isCircleShared = viewModel.isCircleShared(roomId)
            btnPrivate.isChecked = !isCircleShared
            btnPublic.isChecked = isCircleShared
        }
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

    private fun onInputDataChanged() {
        viewModel.handleRoomDataUpdate(
            binding.tilName.getText(),
            binding.tilTopic.getText(),
            binding.btnPublic.isChecked,
            getSelectedAccessLevel()
        )
    }

    private fun getSelectedAccessLevel() =
        AccessLevel.entries.getOrNull(binding.spUserRole.selectedItemPosition)
            ?: AccessLevel.User

}