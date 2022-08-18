package org.futo.circles.feature.sign_up.subscription_stage

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.fragment.ParentBackPressOwnerFragment
import org.futo.circles.databinding.FragmentSubscriptionStageBinding
import org.futo.circles.extensions.observeResponse
import org.futo.circles.extensions.showError
import org.futo.circles.feature.sign_up.subscription_stage.list.SubscriptionsAdapter
import org.futo.circles.subscriptions.ItemPurchasedListener
import org.futo.circles.subscriptions.SubscriptionManagerProvider
import org.koin.androidx.viewmodel.ext.android.viewModel

class SubscriptionStageFragment :
    ParentBackPressOwnerFragment(R.layout.fragment_subscription_stage) {

    private val binding by viewBinding(FragmentSubscriptionStageBinding::bind)
    private val viewModel by viewModel<SubscriptionStageViewModel>()

    private val subscriptionManager by lazy {
        SubscriptionManagerProvider.getManager(
            this, object : ItemPurchasedListener {
                override fun onItemPurchased(purchase: String) {
                    viewModel.validateSubscriptionReceipt(purchase)
                }

                override fun onPurchaseFailed(errorCode: Int) {
                    showError(getString(R.string.purchase_failed_format, errorCode))
                }
            }
        )
    }

    private val listAdapter by lazy {
        SubscriptionsAdapter(onItemClicked = { id -> onSubscriptionSelected(id) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadSubscriptionsList(subscriptionManager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.rvSubscriptions.adapter = listAdapter
    }

    private fun setupObservers() {
        viewModel.subscribeLiveData.observeResponse(this)
        viewModel.purchaseLiveData.observeResponse(this)
        viewModel.subscriptionsListLiveData.observeResponse(this, success = {
            listAdapter.submitList(it)
        })
    }

    private fun onSubscriptionSelected(productId: String) {
        viewModel.purchaseProduct(subscriptionManager, productId)
    }
}