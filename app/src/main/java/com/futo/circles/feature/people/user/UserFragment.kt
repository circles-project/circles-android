package com.futo.circles.feature.people.user

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.databinding.UserFragmentBinding
import com.futo.circles.extensions.loadProfileIcon
import com.futo.circles.extensions.observeData
import com.futo.circles.extensions.setIsVisible
import com.futo.circles.feature.people.user.list.UsersCirclesAdapter
import com.futo.circles.mapping.notEmptyDisplayName
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.matrix.android.sdk.api.session.user.model.User


class UserFragment : Fragment(R.layout.user_fragment) {

    private val args: UserFragmentArgs by navArgs()

    private val viewModel by viewModel<UserViewModel> {
        parametersOf(args.userId)
    }
    private val binding by viewBinding(UserFragmentBinding::bind)

    private val usersCirclesAdapter by lazy { UsersCirclesAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.rvCircles.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = usersCirclesAdapter
        }
    }


    private fun setupObservers() {
        viewModel.userLiveData.observeData(this) {
            it.getOrNull()?.let { user -> setupUserInfo(user) }
        }
        viewModel.usersCirclesLiveData?.observeData(this) {
            usersCirclesAdapter.submitList(it)
            binding.tvEmptyCirclesList.setIsVisible(it.isEmpty())
            binding.tvCirclesListTitle.setIsVisible(it.isNotEmpty())
        }
    }

    private fun setupUserInfo(user: User) {
        with(binding) {
            (activity as? AppCompatActivity)?.supportActionBar?.title = user.notEmptyDisplayName()
            tvUserId.text = user.userId
            tvUserName.text = user.notEmptyDisplayName()
            ivUser.loadProfileIcon(user.avatarUrl, user.notEmptyDisplayName())
            tvCirclesListTitle.text = getString(R.string.users_circles_format, user.notEmptyDisplayName())
            tvEmptyCirclesList.text =
                getString(R.string.not_following_any_circles_format, user.notEmptyDisplayName())
        }
    }

}