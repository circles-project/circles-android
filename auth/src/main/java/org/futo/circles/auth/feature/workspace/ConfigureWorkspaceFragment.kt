package org.futo.circles.auth.feature.workspace

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.FragmentConfigureWorkspaceBinding
import org.futo.circles.auth.feature.workspace.list.WorkspaceTasksListAdapter
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.fragment.HasLoadingState

@AndroidEntryPoint
class ConfigureWorkspaceFragment : Fragment(R.layout.fragment_configure_workspace),
    HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModels<ConfigureWorkspaceViewModel>()

    private val binding by viewBinding(FragmentConfigureWorkspaceBinding::bind)

    private val tasksAdapter by lazy {
        WorkspaceTasksListAdapter { viewModel.onOptionalTaskSelectionChanged(it) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            rvWorkspaceTasks.apply {
                adapter = tasksAdapter
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
            btbCreate.setOnClickListener {
                startLoading(btbCreate)
                viewModel.createWorkspace()
            }
        }
    }

    private fun setupObservers() {
        viewModel.tasksLiveData.observeData(this) {
            tasksAdapter.submitList(it)
        }
        viewModel.workspaceResultLiveData.observeResponse(this,
            success = {
                findNavController()
                    .navigateSafe(ConfigureWorkspaceFragmentDirections.toSetupProfileFragment())
            },
            error = {
                showError(it)
                binding.btbCreate.setText(getString(R.string.retry))
            })
    }
}
