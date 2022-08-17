package org.futo.circles.feature.circles.select

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.SelectRoomsListener
import org.futo.circles.databinding.FragmentSelectCirclesBinding
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.feature.circles.select.list.SelectCirclesAdapter
import org.futo.circles.feature.circles.select.list.SelectedChipsCirclesAdapter
import org.futo.circles.feature.photos.select.SelectRoomsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel


class SelectCirclesFragment : Fragment(R.layout.fragment_select_circles), SelectRoomsFragment {

    private val viewModel by viewModel<SelectCirclesViewModel>()
    private val binding by viewBinding(FragmentSelectCirclesBinding::bind)

    private val selectCirclesAdapter by lazy { SelectCirclesAdapter(viewModel::onCircleSelected) }
    private val selectedCircleAdapter by lazy { SelectedChipsCirclesAdapter(viewModel::onCircleSelected) }

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

    override fun getSelectedRooms() = viewModel.getSelectedCircles()

    private fun setupViews() {
        with(binding) {
            rvCircles.apply {
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                adapter = selectCirclesAdapter
            }
            rvSelectedCircles.adapter = selectedCircleAdapter
        }
    }

    private fun setupObservers() {
        viewModel.circlesLiveData.observeData(this) { items ->
            selectCirclesAdapter.submitList(items)
            val selectedCircles = viewModel.getSelectedCircles()
            selectedCircleAdapter.submitList(selectedCircles)
            binding.selectedCircleDivider.setIsVisible(selectedCircles.isNotEmpty())
            selectRoomsListener?.onRoomsSelected(selectedCircles)
        }
    }
}