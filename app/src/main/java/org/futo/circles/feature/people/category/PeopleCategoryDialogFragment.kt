package org.futo.circles.feature.people.category

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.model.UnIgnoreUser
import org.futo.circles.core.utils.LauncherActivityUtils
import org.futo.circles.core.view.EmptyTabPlaceholderView
import org.futo.circles.databinding.DialogFragmentPeopleCategoryBinding
import org.futo.circles.feature.people.list.PeopleAdapter
import org.futo.circles.model.PeopleCategoryTypeArg

@AndroidEntryPoint
class PeopleCategoryDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentPeopleCategoryBinding::inflate) {

    private val viewModel by viewModels<PeopleCategoryViewModel>()

    private val args: PeopleCategoryDialogFragmentArgs by navArgs()

    private val binding by lazy {
        getBinding() as DialogFragmentPeopleCategoryBinding
    }

    private val usersAdapter by lazy {
        PeopleAdapter(
            onUserClicked = { userId ->
                findNavController().navigateSafe(
                    PeopleCategoryDialogFragmentDirections.toUserFragment(userId)
                )
            },
            onUnIgnore = { userId ->
                withConfirmation(UnIgnoreUser()) { viewModel.unIgnoreUser(userId) }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            val title = getString(
                when (args.categoryType) {
                    PeopleCategoryTypeArg.Connections -> R.string.my_connections
                    PeopleCategoryTypeArg.Followers -> org.futo.circles.core.R.string.my_followers
                    PeopleCategoryTypeArg.Following -> org.futo.circles.core.R.string.people_i_m_following
                    PeopleCategoryTypeArg.Other -> org.futo.circles.core.R.string.other_known_users
                    PeopleCategoryTypeArg.Ignored -> R.string.ignored_users
                }
            )
            toolbar.title = title
            rvUsers.apply {
                setEmptyView(EmptyTabPlaceholderView(requireContext()).apply {
                    setText(getString(R.string.people_category_empty_message, title))
                })
                adapter = usersAdapter
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
        }
    }

    private fun setupObservers() {
        viewModel.usersLiveData.observeData(this) { usersAdapter.submitList(it) }
        viewModel.unIgnoreUserLiveData.observeResponse(this,
            success = {
                (activity as? AppCompatActivity)?.let {
                    LauncherActivityUtils.clearCacheAndRestart(it)
                }
            })
    }

}
