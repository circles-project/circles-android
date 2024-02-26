package org.futo.circles.auth.feature.uia

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.databinding.DialogFragmentUiaBinding
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment

@AndroidEntryPoint
class UIADialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentUiaBinding::inflate) {

    private val viewModel by viewModels<UIAViewModel>()

    private val binding by lazy {
        getBinding() as DialogFragmentUiaBinding
    }

}