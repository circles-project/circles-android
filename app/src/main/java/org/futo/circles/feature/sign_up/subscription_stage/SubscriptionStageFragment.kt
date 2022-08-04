package org.futo.circles.feature.sign_up.subscription_stage

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.fragment.ParentBackPressOwnerFragment
import org.futo.circles.databinding.SubscriptionStageFragmentBinding
import org.futo.circles.extensions.observeResponse
import org.koin.androidx.viewmodel.ext.android.viewModel

class SubscriptionStageFragment :
    ParentBackPressOwnerFragment(R.layout.subscription_stage_fragment) {

    private val binding by viewBinding(SubscriptionStageFragmentBinding::bind)
    private val viewModel by viewModel<SubscriptionStageViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {

        }
    }

    private fun setupObservers() {
        viewModel.subscribeLiveData.observeResponse(this)
    }
}