package org.futo.circles.feature.photos.backup

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.picker.RuntimePermissionHelper
import org.futo.circles.databinding.DialogFragmentMediaBackupBinding
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.setIsVisible
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaBackupDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentMediaBackupBinding::inflate) {

    private val viewModel by viewModel<MediaBackupViewModel>()

    private val binding by lazy {
        getBinding() as DialogFragmentMediaBackupBinding
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val permissionHelper =
        RuntimePermissionHelper(this, Manifest.permission.READ_MEDIA_IMAGES)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
        getFoldersList()
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
        viewModel.mediaFolderLiveData.observeData(this) {
            
        }
    }

    private fun getFoldersList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            permissionHelper.runWithPermission { viewModel.getMediaFolders() }
        else viewModel.getMediaFolders()
    }
}