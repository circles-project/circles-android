package org.futo.circles.auth.feature.workspace

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.FragmentConfigureWorkspaceBinding
import org.futo.circles.auth.feature.workspace.list.WorkspaceTasksListAdapter
import org.futo.circles.core.base.NetworkObserver
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.setEnabledViews
import org.futo.circles.core.extensions.showError

@AndroidEntryPoint
class ConfigureWorkspaceFragment : Fragment(R.layout.fragment_configure_workspace),
    HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModels<ConfigureWorkspaceViewModel>()

    private val binding by viewBinding(FragmentConfigureWorkspaceBinding::bind)

    private val tasksAdapter by lazy {
        WorkspaceTasksListAdapter { viewModel.onOptionalTaskSelectionChanged(it) }
    }

    private var configureWorkspaceListener: ConfigureWorkspaceListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        configureWorkspaceListener = (parentFragment as? ConfigureWorkspaceListener)
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
            btbConfigure.setOnClickListener {
                startLoading(btbConfigure)
                viewModel.createWorkspace()
            }
        }
    }

    private fun setupObservers() {
        NetworkObserver.observe(this) { setEnabledViews(it) }
        viewModel.tasksLiveData.observeData(this) {
            tasksAdapter.submitList(it)
        }
        viewModel.workspaceResultLiveData.observeResponse(this,
            success = {
                configureWorkspaceListener?.onWorkspaceConfigured() ?: kotlin.run {
                    findNavController()
                        .navigateSafe(ConfigureWorkspaceFragmentDirections.toSetupProfileFragment())
                }
            },
            error = {
                showError(it)
                binding.btbConfigure.setText(getString(R.string.retry))
            })
        viewModel.validateWorkspaceResultLiveData.observeResponse(this,
            success = { configureWorkspaceListener?.onWorkspaceConfigured() },
            error = { binding.btbConfigure.setIsLoading(false) }
        )
        viewModel.validationStartedEventLiveData.observeData(this) {
            startLoading(binding.btbConfigure)
        }
    }

    companion object {
        const val SHOULD_VALIDATE = "should_validate"
        fun create(shouldValidate: Boolean) = ConfigureWorkspaceFragment().apply {
            arguments = bundleOf(SHOULD_VALIDATE to shouldValidate)
        }
    }
}
