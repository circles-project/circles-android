package org.futo.circles.auth.feature.sign_up.subscription_stage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.FragmentSubscriptionStageBinding
import org.futo.circles.auth.feature.sign_up.subscription_stage.list.SubscriptionsAdapter
import org.futo.circles.auth.model.SubscriptionReceiptData
import org.futo.circles.auth.subscriptions.ItemPurchasedListener
import org.futo.circles.auth.subscriptions.SubscriptionProvider
import org.futo.circles.core.base.fragment.ParentBackPressOwnerFragment
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showError
import javax.inject.Inject

@AndroidEntryPoint
class SubscriptionStageFragment :
    ParentBackPressOwnerFragment(R.layout.fragment_subscription_stage) {

    private val binding by viewBinding(FragmentSubscriptionStageBinding::bind)
    private val viewModel by viewModels<SubscriptionStageViewModel>()

    @Inject
    lateinit var subscriptionProvider: SubscriptionProvider

    private val subscriptionManager by lazy {
        subscriptionProvider.getManager(
            this, object : ItemPurchasedListener {
                override fun onItemPurchased(subscriptionReceiptData: SubscriptionReceiptData) {
                    viewModel.validateSubscription(subscriptionReceiptData)
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
        viewModel.initiateSubscriptionStage(subscriptionManager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.rvSubscriptions.apply {
            addItemDecoration(
                MaterialDividerItemDecoration(context, DividerItemDecoration.VERTICAL).apply {
                    isLastItemDecorated = false
                })
            adapter = listAdapter
        }
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