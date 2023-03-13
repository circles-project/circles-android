package org.futo.circles.feature.photos.backup

import android.os.Bundle
import android.view.View
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentMediaBackupBinding
import org.futo.circles.extensions.setIsVisible
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaBackupDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentMediaBackupBinding::inflate) {

    private val viewModel by viewModel<MediaBackupViewModel>()

    private val binding by lazy {
        getBinding() as DialogFragmentMediaBackupBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            lBackupSwitchContainer.setOnClickListener {
                svBackup.isChecked = !svBackup.isChecked
            }
            svBackup.setOnCheckedChangeListener { _, isChecked ->
                groupBackupFolders.setIsVisible(isChecked)
            }
        }
    }

    private fun setupObservers() {

    }
}