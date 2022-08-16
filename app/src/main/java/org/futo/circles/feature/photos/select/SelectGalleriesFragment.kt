package org.futo.circles.feature.photos.select

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.databinding.FragmentSelectGalleriesBinding
import org.futo.circles.extensions.observeData
import org.futo.circles.feature.photos.select.list.SelectGalleryAdapter
import org.futo.circles.model.SelectableRoomListItem
import org.koin.androidx.viewmodel.ext.android.viewModel

interface SelectGalleriesListener {
    fun onGallerySelected(galleries: List<SelectableRoomListItem>)
}

class SelectGalleriesFragment : Fragment(R.layout.fragment_select_galleries) {

    private val viewModel by viewModel<SelectGalleriesViewModel>()
    private val binding by viewBinding(FragmentSelectGalleriesBinding::bind)

    private val listAdapter by lazy {
        SelectGalleryAdapter(
            onGalleryClicked = { galleryListItem -> onGallerySelected(galleryListItem) },
        )
    }

    private var selectGalleriesListener: SelectGalleriesListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        selectGalleriesListener = (parentFragment as? SelectGalleriesListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    fun getSelectedGalleries(): List<SelectableRoomListItem> =
        viewModel.galleriesLiveData.value?.filter { it.isSelected } ?: emptyList()

    private fun setupViews() {
        binding.rvGalleries.adapter = listAdapter
    }

    private fun setupObservers() {
        viewModel.galleriesLiveData.observeData(this) {
            listAdapter.submitList(it)
            selectGalleriesListener?.onGallerySelected(it.filter { it.isSelected })
        }
    }

    private fun onGallerySelected(gallery: SelectableRoomListItem) {
        viewModel.toggleGallerySelect(gallery)
    }

}