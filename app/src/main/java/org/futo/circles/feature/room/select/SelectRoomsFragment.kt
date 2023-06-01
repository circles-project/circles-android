package org.futo.circles.feature.room.select

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.base.RoomsListener
import org.futo.circles.core.room.select.SelectRoomsListener
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.databinding.FragmentSelectRoomsBinding
import org.futo.circles.gallery.feature.select.RoomsPicker
import org.futo.circles.feature.room.select.list.SelectRoomsAdapter
import org.futo.circles.feature.room.select.list.SelectedChipsRoomsAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class SelectRoomsFragment : Fragment(R.layout.fragment_select_rooms),
    RoomsPicker {

    @Suppress("DEPRECATION")
    private val viewModel by viewModel<SelectRoomsViewModel> {
        parametersOf(
            CircleRoomTypeArg.values().firstOrNull { it.ordinal == arguments?.getInt(TYPE_ORDINAL) }
                ?: CircleRoomTypeArg.Circle
        )
    }
    private val binding by viewBinding(FragmentSelectRoomsBinding::bind)

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
            binding.selectedCircleDivider.setIsVisible(selectedRooms.isNotEmpty())
            selectRoomsListener?.onRoomsSelected(selectedRooms)
        }
    }

    companion object {
        private const val TYPE_ORDINAL = "type_ordinal"
        fun create(roomType: CircleRoomTypeArg) = SelectRoomsFragment().apply {
            arguments = bundleOf(TYPE_ORDINAL to roomType.ordinal)
        }
    }
}