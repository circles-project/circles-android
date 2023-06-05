package org.futo.circles.feature.timeline.post.emoji

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.vanniktech.emoji.EmojiTheming
import com.vanniktech.emoji.recent.NoRecentEmoji
import com.vanniktech.emoji.search.SearchEmojiManager
import com.vanniktech.emoji.variant.NoVariantEmoji
import org.futo.circles.R
import org.futo.circles.databinding.BottomSheetEmojiBinding

interface EmojiPickerListener {
    fun onEmojiSelected(roomId: String?, eventId: String?, emoji: String)
}

class EmojiBottomSheet : BottomSheetDialogFragment() {

    private var binding: BottomSheetEmojiBinding? = null
    private var emojiPickerListener: EmojiPickerListener? = null
    private val args: EmojiBottomSheetArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        emojiPickerListener =
            parentFragmentManager.fragments.findLast { it is EmojiPickerListener } as? EmojiPickerListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetEmojiBinding.inflate(inflater, container, false)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (dialog as? BottomSheetDialog)?.let {
            it.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        setupViews()
    }

    private fun setupViews() {
        binding?.apply {
            ivClose.setOnClickListener { dismiss() }
            emojiView.apply {
                setUp(
                    requireView(),
                    { emoji ->
                        emojiView
                        onEmojiSelected(emoji.unicode)
                    }, null, null,
                    EmojiTheming(
                        backgroundColor = ContextCompat.getColor(
                            requireContext(),
                            R.color.white
                        ),
                        primaryColor = ContextCompat.getColor(
                            requireContext(),
                            R.color.blue
                        ),
                        secondaryColor = Color.RED,
                        dividerColor = ContextCompat.getColor(
                            requireContext(),
                            org.futo.circles.core.R.color.divider_color
                        ),
                        textColor = ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        ),
                        textSecondaryColor = ContextCompat.getColor(
                            requireContext(),
                            R.color.gray
                        )
                    ),
                    NoRecentEmoji, SearchEmojiManager(), NoVariantEmoji
                )
                tearDown()
            }
        }
    }

    private fun onEmojiSelected(unicode: String) {
        emojiPickerListener?.onEmojiSelected(args.roomId, args.eventId, unicode)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}