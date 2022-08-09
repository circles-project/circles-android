package org.futo.circles.feature.sign_up.sign_up_type

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.BuildConfig
import org.futo.circles.R
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.databinding.SelectSignUpTypeFragmentBinding
import org.futo.circles.extensions.getText
import org.futo.circles.extensions.observeResponse
import org.futo.circles.extensions.setIsVisible
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectSignUpTypeFragment : Fragment(R.layout.select_sign_up_type_fragment), HasLoadingState {

    override val fragment: Fragment = this

    private val binding by viewBinding(SelectSignUpTypeFragmentBinding::bind)
    private val viewModel by viewModel<SelectSignUpTypeViewModel>()

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
    }

    private fun setSignupButtonsEnabled() {
        val isEnabled = binding.tilUserName.editText?.text?.isNotEmpty() == true &&
                binding.tilPassword.editText?.text?.isNotEmpty() == true
        binding.btnToken.isEnabled = isEnabled
        binding.btnSubscription.isEnabled = isEnabled
    }
}