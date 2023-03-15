package org.futo.circles.feature.photos.backup

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.core.picker.RuntimePermissionHelper
import org.futo.circles.databinding.DialogFragmentMediaBackupBinding
import org.futo.circles.extensions.getText
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.feature.photos.backup.list.MediaFoldersListAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaBackupDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentMediaBackupBinding::inflate), HasLoadingState {

    private val viewModel by viewModel<MediaBackupViewModel>()

    override val fragment: Fragment = this
    private val binding by lazy {
        getBinding() as DialogFragmentMediaBackupBinding
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val readMediaPermissionHelper =
        RuntimePermissionHelper(this, Manifest.permission.READ_MEDIA_IMAGES)

    private val foldersAdapter by lazy {
        MediaFoldersListAdapter(
            onItemCheckChanged = { id -> viewModel.onFolderBackupCheckChanged(id) }
        )
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
                if (isChecked) getFoldersList()
            }
            rvDeviceFolders.apply {
                adapter = foldersAdapter
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
            btnSave.setOnClickListener {
                viewModel.saveBackupSettings()
                startLoading(btnSave)
            }
        }
    }

    private fun setupObservers() {
        viewModel.mediaFolderLiveData.observeData(this) {
            foldersAdapter.submitList(it)
        }
    }

    private fun getFoldersList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            readMediaPermissionHelper.runWithPermission { viewModel.getMediaFolders() }
        else viewModel.getMediaFolders()
    }
}