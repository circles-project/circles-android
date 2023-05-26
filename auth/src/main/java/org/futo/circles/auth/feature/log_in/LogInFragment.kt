package org.futo.circles.auth.feature.log_in

import android.os.Bundle
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.auth.R
import org.futo.circles.auth.base.AuthNavigator
import org.futo.circles.auth.databinding.FragmentLogInBinding
import org.futo.circles.auth.feature.log_in.switch_user.list.SwitchUsersAdapter
import org.futo.circles.auth.feature.log_in.switch_user.list.SwitchUsersViewHolder
import org.futo.circles.auth.model.RemoveUser
import org.futo.circles.core.CirclesAppConfig
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.fragment.HasLoadingState
import org.koin.androidx.viewmodel.ext.android.viewModel


class LogInFragment : Fragment(R.layout.fragment_log_in), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModel<LogInViewModel>()
    private val binding by viewBinding(FragmentLogInBinding::bind)

    private val autocompleteAdapter by lazy {
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            listOf(CirclesAppConfig.usServerDomain, CirclesAppConfig.euServerDomain)
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
                        if (!hasFocus && tvDomain.text.isEmpty()) CirclesAppConfig.usServerDomain
                        else getString(R.string.domain)
                }
            }
            tilDomain.hint = CirclesAppConfig.usServerDomain
            binding.rvSwitchUsers.apply {
                adapter = switchUsersAdapter
                addItemDecoration(
                    org.futo.circles.core.list.BaseRvDecoration.OffsetDecoration<SwitchUsersViewHolder>(
                        16
                    )
                )
            }
        }
    }

    private fun setupObservers() {
        viewModel.loginResultLiveData.observeResponse(this,
            success = {
                AuthNavigator.navigateToLoginStages(requireContext(), findNavController())
            }
        )
        viewModel.switchUsersLiveData.observeData(this) {
            binding.tvResumeSession.setIsVisible(it.isNotEmpty())
            switchUsersAdapter.submitList(it)
        }
        viewModel.navigateToBottomMenuScreenLiveData.observeData(this) {
            AuthNavigator.navigateToBottomMenu(requireContext(), findNavController())
        }
    }

    private fun setOnClickActions() {
        with(binding) {
            btnSignUp.setOnClickListener {
                AuthNavigator.navigateToSignUp(requireContext(), findNavController())
            }
            btnLogin.setOnClickListener {
                val userName = binding.tilUserName.getText()
                if (userName.isEmpty()) {
                    showError(getString(R.string.username_can_not_be_empty))
                    return@setOnClickListener
                }
                startLoading(btnLogin)
                viewModel.startLogInFlow(userName, getDomain())
            }
        }
    }

    private fun getDomain() = binding.tvDomain.text.toString().takeIf { it.isNotEmpty() }
        ?: CirclesAppConfig.usServerDomain
}