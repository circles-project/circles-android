package org.futo.circles.auth.feature.sign_up.sign_up_type

import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.FragmentSelectSignUpTypeBinding
import org.futo.circles.auth.subscriptions.SubscriptionProvider
import org.futo.circles.core.base.CirclesAppConfig
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.base.fragment.HasLoadingState
import javax.inject.Inject

@AndroidEntryPoint
class SelectSignUpTypeFragment : Fragment(R.layout.fragment_select_sign_up_type),
    HasLoadingState {

    override val fragment: Fragment = this

    private val binding by viewBinding(FragmentSelectSignUpTypeBinding::bind)
    private val viewModel by viewModels<SelectSignUpTypeViewModel>()

    @Inject
    lateinit var subscriptionProvider: SubscriptionProvider

    private val subscriptionManager by lazy {
        subscriptionProvider.getManager(this, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (CirclesAppConfig.isSubscriptionsEnabled)
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
            groupSubscription.setIsVisible(CirclesAppConfig.isSubscriptionsEnabled)
            btnEU.text = Html.fromHtml(
                getString(R.string.eu_server_format, CirclesAppConfig.euServerDomain),
                Html.FROM_HTML_MODE_COMPACT
            )
            btnUS.text =
                Html.fromHtml(
                    getString(R.string.us_server_format, CirclesAppConfig.usServerDomain),
                    Html.FROM_HTML_MODE_COMPACT
                )
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
        binding.btnUS.id -> CirclesAppConfig.usServerDomain
        binding.btnEU.id -> CirclesAppConfig.euServerDomain
        else -> CirclesAppConfig.usServerDomain
    }
}