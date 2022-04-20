package com.futo.circles.feature.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.futo.circles.databinding.ChangeAccessLevelBottomSheetBinding
import com.futo.circles.databinding.CreatePostBottomSheetBinding
import com.futo.circles.feature.group_members.change_role.list.ChangeAccessLevelAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreatePostBottomSheet : BottomSheetDialogFragment() {

    private var binding: CreatePostBottomSheetBinding? = null
    private val viewModel by viewModel<CreatePostViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = CreatePostBottomSheetBinding.inflate(inflater, container, false)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {

    }

    private fun setupObservers() {

    }

}