package org.futo.circles.auth.feature.setup.circles

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.databinding.FragmentSetupCirclesBinding
import org.futo.circles.auth.feature.setup.circles.list.SetupCirclesAdapter
import org.futo.circles.core.base.fragment.BaseBindingFragment

@AndroidEntryPoint
class SetupCirclesFragment :
    BaseBindingFragment<FragmentSetupCirclesBinding>(FragmentSetupCirclesBinding::inflate) {

    private val viewModel by viewModels<SetupCirclesViewModel>()
    private val listAdapter by lazy {
        SetupCirclesAdapter(
            onChangeImage = { id ->

            },
            onRemove = { id ->

            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            rvCircles.apply {
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                adapter = listAdapter
            }
            btnAdd.setOnClickListener { }
            btnNext.setOnClickListener { }
        }
    }

    private fun setupObservers() {

    }

}