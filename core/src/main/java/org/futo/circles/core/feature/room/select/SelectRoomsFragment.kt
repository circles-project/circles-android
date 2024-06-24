package org.futo.circles.core.feature.room.select

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.base.fragment.BaseBindingFragment
import org.futo.circles.core.databinding.FragmentSelectRoomsBinding
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.feature.room.select.interfaces.RoomsListener
import org.futo.circles.core.feature.room.select.interfaces.RoomsPicker
import org.futo.circles.core.feature.room.select.interfaces.SelectRoomsListener
import org.futo.circles.core.feature.room.select.list.SelectRoomsAdapter
import org.futo.circles.core.feature.room.select.list.SelectedChipsRoomsAdapter
import org.futo.circles.core.model.SelectRoomTypeArg

@AndroidEntryPoint
class SelectRoomsFragment :
    BaseBindingFragment<FragmentSelectRoomsBinding>(FragmentSelectRoomsBinding::inflate),
    RoomsPicker {

    private val viewModel by viewModels<SelectRoomsViewModel>()

    private val selectRoomsAdapter by lazy { SelectRoomsAdapter(viewModel::onRoomSelected) }
    private val selectedRoomsAdapter by lazy { SelectedChipsRoomsAdapter(viewModel::onRoomSelected) }

    override var selectRoomsListener: SelectRoomsListener? = null
    private var roomsChangedListener: RoomsListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        roomsChangedListener = (parentFragment as? RoomsListener)
        selectRoomsListener = (parentFragment as? SelectRoomsListener)
        if (selectRoomsListener == null)
            selectRoomsListener = activity as? SelectRoomsListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    override fun getSelectedRooms() = viewModel.getSelectedRooms()

    private fun setupViews() {
        with(binding) {
            rvCircles.apply {
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                adapter = selectRoomsAdapter
            }
            rvSelectedCircles.adapter = selectedRoomsAdapter
        }
    }

    private fun setupObservers() {
        viewModel.roomsLiveData.observeData(this) { items ->
            selectRoomsAdapter.submitList(items)
            roomsChangedListener?.onRoomsListChanged(items)
            val selectedRooms = viewModel.getSelectedRooms()
            selectedRoomsAdapter.submitList(selectedRooms)
            binding.tvSelectedRoomsPlaceholder.setIsVisible(selectedRooms.isEmpty())
            selectRoomsListener?.onRoomsSelected(selectedRooms)
        }
    }

    companion object {
        const val TYPE_ORDINAL = "type_ordinal"
        const val USER_ID = "userId"
        fun create(roomType: SelectRoomTypeArg, userId: String? = null) =
            SelectRoomsFragment().apply {
                arguments = bundleOf(
                    TYPE_ORDINAL to roomType.ordinal,
                    USER_ID to userId
                )
            }
    }
}