package org.futo.circles.auth.feature.uia.stages.username

import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.databinding.FragmentUsernameBinding
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.base.fragment.ParentBackPressOwnerFragment
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse

@AndroidEntryPoint
class UsernameFragment :
    ParentBackPressOwnerFragment<FragmentUsernameBinding>(FragmentUsernameBinding::inflate),
    HasLoadingState {

    private val viewModel by viewModels<UsernameViewModel>()
    override val fragment: Fragment = this

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        setContinueButtonEnabled()
    }

    private fun setupViews() {
        with(binding) {
            btnSetUsername.setOnClickListener {
                startLoading(btnSetUsername)
                viewModel.setUsername(tilUserName.getText())
            }
            tilUserName.editText?.doAfterTextChanged {
                setContinueButtonEnabled()
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
        viewModel.usernameResponseLiveData.observeResponse(this)
        viewModel.domainLiveData.observeData(this) {
            binding.tvServerDomain.text = it
        }
    }

    private fun setContinueButtonEnabled() {
        binding.btnSetUsername.isEnabled = binding.tilUserName.getText().isNotEmpty()
    }

}
