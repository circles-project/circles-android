package org.futo.circles.gallery.feature.select

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.model.SelectableRoomListItem
import org.futo.circles.core.room.select.RoomsPicker
import org.futo.circles.core.room.select.SelectRoomsListener
import org.futo.circles.gallery.R
import org.futo.circles.gallery.databinding.FragmentSelectGalleriesBinding
import org.futo.circles.gallery.feature.select.list.SelectGalleryAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectGalleriesFragment : Fragment(R.layout.fragment_select_galleries), RoomsPicker {

    private val viewModel by viewModel<SelectGalleriesViewModel>()
    private val binding by viewBinding(FragmentSelectGalleriesBinding::bind)

    private val listAdapter by lazy {
        SelectGalleryAdapter(
            onGalleryClicked = { galleryListItem -> onGallerySelected(galleryListItem) },
        )
    }

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

    override fun getSelectedRooms(): List<SelectableRoomListItem> =
        viewModel.galleriesLiveData.value?.filter { it.isSelected } ?: emptyList()

    private fun setupViews() {
        binding.rvGalleries.adapter = listAdapter
    }

    private fun setupObservers() {
        viewModel.galleriesLiveData.observeData(this) {
            listAdapter.submitList(it)
            selectRoomsListener?.onRoomsSelected(it.filter { it.isSelected })
        }
    }

    private fun onGallerySelected(gallery: SelectableRoomListItem) {
        viewModel.toggleGallerySelect(gallery)
    }

}