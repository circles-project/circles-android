package org.futo.circles.feature.photos.backup

import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentMediaBackupBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaBackupDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentMediaBackupBinding::inflate) {

    private val viewModel by viewModel<MediaBackupViewModel>()

    private val binding by lazy {
        getBinding() as DialogFragmentMediaBackupBinding
    }
}