package org.futo.circles.feature.sign_up.sign_up_type

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.BuildConfig
import org.futo.circles.R
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.databinding.FragmentSelectSignUpTypeBinding
import org.futo.circles.extensions.getText
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.observeResponse
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.subscriptions.SubscriptionManagerProvider
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectSignUpTypeFragment : Fragment(R.layout.fragment_select_sign_up_type), HasLoadingState {

    override val fragment: Fragment = this

    private val binding by viewBinding(FragmentSelectSignUpTypeBinding::bind)
    private val viewModel by viewModel<SelectSignUpTypeViewModel>()

    private val subscriptionManager by lazy {
        SubscriptionManagerProvider.getManager(this, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.IS_SUBSCRIPTIONS_ENABLED)
            viewModel.getLastActiveSubscriptionReceipt(subscriptionManager)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.clearSubtitle()
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            groupSubscription.setIsVisible(BuildConfig.IS_SUBSCRIPTIONS_ENABLED)
            btnEU.text = getString(R.string.eu_server_format, BuildConfig.EU_SERVER_DOMAIN)
            btnUS.text = getString(R.string.us_server_format, BuildConfig.EU_SERVER_DOMAIN)
            btnToken.setOnClickListener {
                startLoading(btnToken)
                viewModel.startSignUp(getDomain())
            }
            btnSubscription.setOnClickListener {
                startLoading(btnSubscription)
                viewModel.startSignUp(getDomain(), true)
            }
        }
    }

    private fun setupObservers() {
        viewModel.startSignUpEventLiveData.observeResponse(this)
        viewModel.isSubscribedLiveData.observeData(this) { isSubscribed ->
            binding.tvSubscriptionTitle.text =
                getString(
                    if (isSubscribed) R.string.sign_up_using_active_subscription
                    else R.string.create_a_subscription
                )
            binding.btnSubscription.setText(
                getString(if (isSubscribed) R.string.sign_up else R.string.choose_a_subscription)
            )
        }
    }

    private fun getDomain() = when (binding.serverLocationGroup.checkedRadioButtonId) {
        binding.btnUS.id -> BuildConfig.US_SERVER_DOMAIN
        binding.btnEU.id -> BuildConfig.EU_SERVER_DOMAIN
        else -> BuildConfig.US_SERVER_DOMAIN
    }
}