package com.futo.circles.feature.emoji

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.futo.circles.databinding.EmojiBottomSheetBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.model.EmojiCategory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

interface EmojiPickerListener {
    fun onEmojiSelected()
}

class EmojiBottomSheet : BottomSheetDialogFragment() {

    private var binding: EmojiBottomSheetBinding? = null
    private val viewModel by viewModel<EmojiViewModel>()
    private var emojiPickerListener: EmojiPickerListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        emojiPickerListener =
            parentFragmentManager.fragments.firstOrNull { it is EmojiPickerListener } as? EmojiPickerListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = EmojiBottomSheetBinding.inflate(inflater, container, false)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.let {
            (it as? BottomSheetDialog)?.behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        }
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding?.ivClose?.setOnClickListener { dismiss() }
    }

    private fun setupObservers() {
        viewModel.categoriesLiveData.observeData(this) { setupEmojiCategories(it) }
    }

    private fun setupEmojiCategories(categories: List<EmojiCategory>) {
        binding?.let { binding ->
            categories.forEach { category ->
                binding.tabs.newTab().apply {
                    text = category.emojiTitle
                    contentDescription = category.name
                }.also { tab ->
                    binding.tabs.addTab(tab)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}