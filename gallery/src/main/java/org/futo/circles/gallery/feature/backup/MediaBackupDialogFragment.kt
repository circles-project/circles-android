package org.futo.circles.gallery.feature.backup

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.gallery.R
import org.futo.circles.gallery.databinding.DialogFragmentMediaBackupBinding
import org.futo.circles.gallery.feature.backup.list.MediaFoldersListAdapter

@AndroidEntryPoint
class MediaBackupDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentMediaBackupBinding>(DialogFragmentMediaBackupBinding::inflate),
    HasLoadingState {

    private val viewModel by viewModels<MediaBackupViewModel>()

    override val fragment: Fragment = this

    private val foldersAdapter by lazy {
        MediaFoldersListAdapter(
            onItemCheckChanged = { id, isSelected ->
                viewModel.onFolderBackupCheckChanged(
                    id,
                    isSelected,
                    binding.svBackup.isChecked,
                    binding.svWiFi.isChecked,
                    binding.svCompress.isChecked
                )
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
            lWifi.setOnClickListener {
                svWiFi.isChecked = !svWiFi.isChecked
            }
            lCompressed.setOnClickListener {
                svCompress.isChecked = !svCompress.isChecked
            }
            svBackup.setOnCheckedChangeListener { _, isChecked ->
                groupBackupFolders.setIsVisible(isChecked)
                onInputDataChanged()
            }
            svWiFi.setOnCheckedChangeListener { _, _ -> onInputDataChanged() }
            svCompress.setOnCheckedChangeListener { _, _ -> onInputDataChanged() }
            rvDeviceFolders.apply {
                adapter = foldersAdapter
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }
            btnSave.setOnClickListener {
                viewModel.saveBackupSettings(
                    svBackup.isChecked,
                    svWiFi.isChecked,
                    svCompress.isChecked
                )
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
                showSuccess(getString(R.string.saved))
                onBackPressed()
            })
        viewModel.initialBackupSettingsLiveData.observeData(this) {
            binding.svBackup.isChecked = it.isBackupEnabled
            binding.svWiFi.isChecked = it.backupOverWifi
            binding.svCompress.isChecked = it.compressBeforeSending
        }
        viewModel.isSettingsDataChangedLiveData.observeData(this) {
            binding.btnSave.isEnabled = it
        }
    }

    private fun onInputDataChanged() {
        viewModel.handleDataSettingsChanged(
            binding.svBackup.isChecked,
            binding.svWiFi.isChecked,
            binding.svCompress.isChecked
        )
    }
}