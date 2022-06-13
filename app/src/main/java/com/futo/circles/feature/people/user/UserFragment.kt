package com.futo.circles.feature.people.user

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.databinding.UserFragmentBinding
import com.futo.circles.extensions.loadProfileIcon
import com.futo.circles.extensions.observeData
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.matrix.android.sdk.api.session.user.model.User


class UserFragment : Fragment(R.layout.user_fragment) {

    private val args: UserFragmentArgs by navArgs()

    private val viewModel by viewModel<UserViewModel>() {
        parametersOf(args.userId)
    }
    private val binding by viewBinding(UserFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }


    private fun setupObservers() {
        viewModel.userLiveData.observeData(this) {
            it.getOrNull()?.let { user -> setupUserInfo(user) }
        }
    }

    private fun setupUserInfo(user: User) {
        with(binding) {
            (activity as? AppCompatActivity)?.supportActionBar?.title = user.userId
            tvUserId.text = user.userId
            tvUserName.text = user.displayName
            ivUser.loadProfileIcon(user.avatarUrl, user.displayName ?: "")
        }
    }

}