package org.futo.circles.feature.rageshake

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.databinding.DialogFragmentBugReportBinding

class BugReportDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentBugReportBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this

    private val binding by lazy {
        getBinding() as DialogFragmentBugReportBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.toolbar.apply { setNavigationOnClickListener { dismiss() } }
    }

    companion object {
        fun show(activity: AppCompatActivity) =
            BugReportDialogFragment().show(
                activity.supportFragmentManager, "BugReportDialogFragment"
            )
    }
}