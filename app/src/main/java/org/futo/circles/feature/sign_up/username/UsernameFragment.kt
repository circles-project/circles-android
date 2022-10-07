package org.futo.circles.feature.sign_up.username

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.core.fragment.ParentBackPressOwnerFragment
import org.futo.circles.databinding.FragmentUsernameBinding
import org.futo.circles.extensions.getText
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.observeResponse
import org.koin.androidx.viewmodel.ext.android.viewModel

class UsernameFragment : ParentBackPressOwnerFragment(R.layout.fragment_username), HasLoadingState {

    private val viewModel by viewModel<UsernameViewModel>()
    override val fragment: Fragment = this
    private val binding by viewBinding(FragmentUsernameBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            btnSetUsername.setOnClickListener {
                startLoading(btnSetUsername)
                viewModel.setUsername(tilUserName.getText())
            }
            tilUserName.editText?.doAfterTextChanged {
                btnSetUsername.isEnabled = tilUserName.getText().isNotEmpty()
            }
        }
    }

    private fun setupObservers() {
        viewModel.usernameResponseLiveData.observeResponse(this)
        viewModel.domainLiveData.observeData(this){
            binding.tvServerDomain.text = it
        }
    }
}
