package org.futo.circles.settings.feature.manage_subscription

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.DialogFragmentManageSubscriptionBinding
import org.futo.circles.auth.model.ActiveSubscriptionInfo
import org.futo.circles.auth.subscriptions.SubscriptionProvider
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.visible
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class ManageSubscriptionDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentManageSubscriptionBinding::inflate) {

    private val viewModel by viewModels<ManageSubscriptionViewModel>()

    private val binding by lazy {
        getBinding() as DialogFragmentManageSubscriptionBinding
    }

    @Inject
    lateinit var subscriptionProvider: SubscriptionProvider

    private val subscriptionManager by lazy {
        subscriptionProvider.getManager(this, null)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getSubscriptionInfo(subscriptionManager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {

    }

    private fun setupObservers() {
        viewModel.subscriptionInfoLiveData.observeResponse(
            this,
            onRequestInvoked = { binding.vLoading.gone() },
            success = {
                binding.lSubscriptionInfo.visible()
                bindSubscriptionInfo(it)
            },
            error = {
                binding.tvEmptyMessage.visible()
            })
    }

    private fun bindSubscriptionInfo(subscriptionInfo: ActiveSubscriptionInfo) {
        with(binding) {
            tvName.text = subscriptionInfo.name
            tvDescription.text = subscriptionInfo.description
            tvPrice.text = getString(R.string.price_format, subscriptionInfo.price)
            tvDuration.text = getString(R.string.duration_format, subscriptionInfo.duration)
            tvProductId.text = getString(R.string.product_id_format, subscriptionInfo.productId)
            val purchaseDate =
                DateFormat.format("MMM dd yyyy, h:mm a", Date(subscriptionInfo.purchaseTime))
            tvPurchasedAt.text = getString(R.string.purchase_time_format, purchaseDate)
            tvAutoRenewMessage.text = getString(
                if (subscriptionInfo.isAutoRenewing) R.string.is_auto_renew_message
                else R.string.is_not_auto_renew_message
            )
            btnManageGp.setOnClickListener {
                openPlayStoreSubscriptionInfo(
                    subscriptionInfo.productId,
                    subscriptionInfo.packageName
                )
            }
        }
    }

    private fun openPlayStoreSubscriptionInfo(sku: String, packageName: String) {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/account/subscriptions?sku=$sku&package=$packageName")
                )
            )
        } catch (e: ActivityNotFoundException) {
            showError(getString(R.string.can_not_open_google_play))
        }
    }
}