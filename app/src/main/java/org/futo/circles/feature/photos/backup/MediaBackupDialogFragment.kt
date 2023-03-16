package org.futo.circles.feature.photos.backup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import org.futo.circles.R
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.databinding.DialogFragmentMediaBackupBinding
import org.futo.circles.extensions.*
import org.futo.circles.feature.photos.backup.list.MediaFoldersListAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaBackupDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentMediaBackupBinding::inflate), HasLoadingState {

    private val viewModel by viewModel<MediaBackupViewModel>()

    override val fragment: Fragment = this
    private val binding by lazy {
        getBinding() as DialogFragmentMediaBackupBinding
    }

    private val foldersAdapter by lazy {
        MediaFoldersListAdapter(
            onItemCheckChanged = { id, isSelected ->
                viewModel.onFolderBackupCheckChanged(id, isSelected, binding.svBackup.isChecked)
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
            lBackupSwitchContainer.setOnClickListener {
                svBackup.isChecked = !svBackup.isChecked
            }
            svBackup.setOnCheckedChangeListener { _, isChecked ->
                groupBackupFolders.setIsVisible(isChecked)
                onInputDataChanged()
            }
            rvDeviceFolders.apply {
                adapter = foldersAdapter
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
            btnSave.setOnClickListener {
                viewModel.saveBackupSettings(svBackup.isChecked)
                startLoading(btnSave)
            }
        }
    }

    private fun setupObservers() {
        viewModel.mediaFolderLiveData.observeData(this) {
            foldersAdapter.submitList(it)
        }
        viewModel.saveBackupSettingsResultLiveData.observeResponse(this,
            success = {
                showSuccess(getString(R.string.saved), true)
                onBackPressed()
            })
        viewModel.initialBackupSettingsLiveData.observeData(this) {
            binding.svBackup.isChecked = it.isBackupEnabled
        }
        viewModel.isSettingsDataChangedLiveData.observeData(this) {
            binding.btnSave.isEnabled = it
        }
    }

    private fun onInputDataChanged() {
        viewModel.handleDataSettingsChanged(binding.svBackup.isChecked)
    }
}