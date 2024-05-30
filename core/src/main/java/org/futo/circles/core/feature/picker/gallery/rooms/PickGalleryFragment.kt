package org.futo.circles.core.feature.picker.gallery.rooms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.databinding.FragmentPickGalleryBinding
import org.futo.circles.core.databinding.FragmentSelectUsersBinding
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.feature.picker.gallery.PickGalleryMediaViewModel
import org.futo.circles.core.feature.picker.gallery.rooms.list.PickGalleryListAdapter

@AndroidEntryPoint
class PickGalleryFragment : Fragment() {

    private val viewModel by viewModels<PickGalleryViewModel>()
    private val parentViewModel by viewModels<PickGalleryMediaViewModel>({ requireParentFragment() })
    private var _binding: FragmentPickGalleryBinding? = null
    private val binding get() = _binding!!

    private val listAdapter by lazy {
        PickGalleryListAdapter(onRoomClicked = { gallery ->
            parentViewModel.onGalleryChosen(gallery.id)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPickGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.rvRooms.adapter = listAdapter
    }

    private fun setupObservers() {
        viewModel.galleriesLiveData.observeData(this) { listAdapter.submitList(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}