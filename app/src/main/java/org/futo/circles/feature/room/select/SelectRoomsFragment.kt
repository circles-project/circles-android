package org.futo.circles.feature.room.select

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.SelectRoomsListener
import org.futo.circles.databinding.FragmentSelectRoomsBinding
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.feature.room.select.list.SelectRoomsAdapter
import org.futo.circles.feature.room.select.list.SelectedChipsRoomsAdapter
import org.futo.circles.feature.photos.select.SelectRoomsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class SelectRoomsFragment : Fragment(R.layout.fragment_select_rooms),
    SelectRoomsFragment {

    private val viewModel by viewModel<SelectRoomsViewModel>()
    private val binding by viewBinding(FragmentSelectRoomsBinding::bind)

    private val selectRoomsAdapter by lazy { SelectRoomsAdapter(viewModel::onRoomSelected) }
    private val selectedCircleAdapter by lazy { SelectedChipsRoomsAdapter(viewModel::onRoomSelected) }

    override var selectRoomsListener: SelectRoomsListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
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
            rvSelectedCircles.adapter = selectedCircleAdapter
        }
    }

    private fun setupObservers() {
        viewModel.roomsLiveData.observeData(this) { items ->
            selectRoomsAdapter.submitList(items)
            val selectedCircles = viewModel.getSelectedRooms()
            selectedCircleAdapter.submitList(selectedCircles)
            binding.selectedCircleDivider.setIsVisible(selectedCircles.isNotEmpty())
            selectRoomsListener?.onRoomsSelected(selectedCircles)
        }
    }
}