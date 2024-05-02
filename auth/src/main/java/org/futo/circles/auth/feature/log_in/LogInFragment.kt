package org.futo.circles.auth.feature.log_in

import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.FragmentLogInBinding
import org.futo.circles.auth.feature.log_in.switch_user.list.SwitchUsersAdapter
import org.futo.circles.auth.feature.log_in.switch_user.list.SwitchUsersViewHolder
import org.futo.circles.auth.model.ForgotPassword
import org.futo.circles.auth.model.RemoveUser
import org.futo.circles.core.base.CirclesAppConfig
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.base.list.BaseRvDecoration
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.withConfirmation


@AndroidEntryPoint
class LogInFragment : Fragment(R.layout.fragment_log_in), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModels<LogInViewModel>()
    private val binding by viewBinding(FragmentLogInBinding::bind)

    private val autocompleteAdapter by lazy {
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            CirclesAppConfig.serverDomains
        )
    }

    private val switchUsersAdapter by lazy {
        SwitchUsersAdapter(
            onResumeClicked = { id ->
                startLoading(binding.btnLogin)
                viewModel.resumeSwitchUserSession(id)
            },
            onRemoveClicked = { id ->
                withConfirmation(RemoveUser()) {
                    viewModel.removeSwitchUser(id)
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setOnClickActions()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            tvDomain.apply {
                setAdapter(autocompleteAdapter)
                onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
                    tilDomain.hint =
                        if (!hasFocus && tvDomain.text.isEmpty()) CirclesAppConfig.serverDomains.first()
                        else getString(R.string.domain)
                }
            }
            tilDomain.hint = CirclesAppConfig.serverDomains.first()
            rvSwitchUsers.apply {
                adapter = switchUsersAdapter
                addItemDecoration(BaseRvDecoration.OffsetDecoration<SwitchUsersViewHolder>(16))
            }
            etUserName.filters = arrayOf<InputFilter>(object : InputFilter.AllCaps() {
                override fun filter(
                    source: CharSequence?,
                    start: Int,
                    end: Int,
                    dest: Spanned?,
                    dstart: Int,
                    dend: Int
                ) = source.toString().lowercase()
            })
        }
    }

    private fun setupObservers() {
        viewModel.loginResultLiveData.observeResponse(this,
            success = {
                findNavController().navigateSafe(LogInFragmentDirections.toUiaFragment())
            },
            error = {
                showError(getString(R.string.username_not_found))
            }
        )
        viewModel.switchUsersLiveData.observeData(this) {
            binding.tvResumeSession.setIsVisible(it.isNotEmpty())
            switchUsersAdapter.submitList(it)
        }
        viewModel.navigateToBottomMenuScreenLiveData.observeData(this) {
            findNavController().navigateSafe(LogInFragmentDirections.toHomeFragment())
        }
    }

    private fun setOnClickActions() {
        with(binding) {
            btnSignUp.setOnClickListener {
                findNavController().navigateSafe(LogInFragmentDirections.toSignUpFragment())
            }
            btnLogin.setOnClickListener { startLogin(false) }
            btnForgotPassword.setOnClickListener {
                withConfirmation(ForgotPassword()) { startLogin(true) }
            }
        }
    }

    private fun startLogin(isForgotPassword: Boolean) {
        val userName = binding.tilUserName.getText()
        if (userName.isEmpty()) {
            showError(getString(R.string.username_can_not_be_empty))
            return
        }
        startLoading(binding.btnLogin)
        viewModel.startLogInFlow(userName, getDomain(), isForgotPassword)
    }

    private fun getDomain() = binding.tvDomain.text.toString().takeIf { it.isNotEmpty() }
        ?: CirclesAppConfig.serverDomains.first()
}