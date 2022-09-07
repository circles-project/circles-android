package org.futo.circles.feature.sign_up.sign_up_type

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
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
            tilUserName.editText?.doAfterTextChanged { setSignupButtonsEnabled() }
            tilPassword.editText?.doAfterTextChanged { setSignupButtonsEnabled() }
            btnToken.setOnClickListener {
                startLoading(btnToken)
                viewModel.startSignUp(
                    tilUserName.getText(),
                    tilPassword.getText(),
                    tvServerDomain.text.toString()
                )
            }
            btnSubscription.setOnClickListener {
                startLoading(btnSubscription)
                viewModel.startSignUp(
                    tilUserName.getText(),
                    tilPassword.getText(),
                    tvServerDomain.text.toString(),
                    true
                )
            }
            serverLocationGroup.setOnCheckedChangeListener { _, checkedId ->
                tvServerDomain.text = when (checkedId) {
                    btnUS.id -> BuildConfig.US_SERVER_DOMAIN
                    btnEU.id -> BuildConfig.EU_SERVER_DOMAIN
                    else -> BuildConfig.US_SERVER_DOMAIN
                }
            }
            serverLocationGroup.check(btnUS.id)
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

    private fun setSignupButtonsEnabled() {
        val isEnabled = binding.tilUserName.editText?.text?.isNotEmpty() == true &&
                binding.tilPassword.editText?.text?.isNotEmpty() == true
        binding.btnToken.isEnabled = isEnabled
        binding.btnSubscription.isEnabled = isEnabled
    }
}