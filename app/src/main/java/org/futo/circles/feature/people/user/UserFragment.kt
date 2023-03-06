package org.futo.circles.feature.people.user

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.databinding.FragmentUserBinding
import org.futo.circles.extensions.*
import org.futo.circles.feature.people.user.list.UsersCirclesAdapter
import org.futo.circles.mapping.notEmptyDisplayName
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.matrix.android.sdk.api.session.user.model.User


class UserFragment : Fragment(R.layout.fragment_user) {

    private val args: UserFragmentArgs by navArgs()

    private val viewModel by viewModel<UserViewModel> {
        parametersOf(args.userId)
    }
    private val binding by viewBinding(FragmentUserBinding::bind)

    private val usersCirclesAdapter by lazy {
        UsersCirclesAdapter(
            onRequestFollow = { timelineId -> viewModel.requestFollowTimeline(timelineId) }
        )
    }

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
        viewModel.userLiveData.observeData(this) { setupUserInfo(it) }
        viewModel.timelineLiveDataLiveData.observeData(this) {
            usersCirclesAdapter.submitList(it)
            binding.tvEmptyCirclesList.setIsVisible(it.isEmpty())
        }
        viewModel.requstFollowLiveData.observeResponse(this,
            success = { showSuccess(getString(R.string.request_sent)) })
    }

    private fun setupUserInfo(user: User) {
        with(binding) {
            (activity as? AppCompatActivity)?.supportActionBar?.title = user.notEmptyDisplayName()
            tvUserId.text = user.userId
            tvUserName.text = user.notEmptyDisplayName()
            ivUser.loadProfileIcon(user.avatarUrl, user.notEmptyDisplayName())
            tvEmptyCirclesList.text =
                getString(R.string.not_following_any_circles_format, user.notEmptyDisplayName())
        }
    }

}