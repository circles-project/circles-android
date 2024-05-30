package org.futo.circles.auth.feature.workspace

import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.DialogFragmentWorkspaceBinding
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.extensions.showSuccess

@AndroidEntryPoint
class WorkspaceDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentWorkspaceBinding>(DialogFragmentWorkspaceBinding::inflate),
    ConfigureWorkspaceListener {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = false
        if (savedInstanceState == null) addConfigureWorkspaceFragment()
    }

    private fun addConfigureWorkspaceFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.lContainer, ConfigureWorkspaceFragment.create(true))
            .commitAllowingStateLoss()
    }

    override fun onWorkspaceConfigured() {
        showSuccess(getString(R.string.workspace_configured))
        dismiss()
    }
}