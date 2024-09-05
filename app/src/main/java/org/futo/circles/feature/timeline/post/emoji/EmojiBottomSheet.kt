package org.futo.circles.feature.timeline.post.emoji

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.vanniktech.emoji.Emoji
import com.vanniktech.emoji.EmojiTheming
import org.futo.circles.core.base.fragment.TransparentBackgroundBottomSheetDialogFragment
import org.futo.circles.core.extensions.isNightMode
import org.futo.circles.databinding.BottomSheetEmojiBinding

interface EmojiPickerListener {
    fun onEmojiSelected(roomId: String?, eventId: String?, emoji: String)
}

class EmojiBottomSheet : TransparentBackgroundBottomSheetDialogFragment() {

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
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding?.apply {
            emojiView.apply {
                setUp(
                    requireView(),
                    { emoji ->
                        emojiView
                        onEmojiSelected(emoji)
                    }, null, null,
                    EmojiTheming(
                        backgroundColor = ContextCompat.getColor(
                            requireContext(),
                            org.futo.circles.core.R.color.post_card_background_color
                        ),
                        primaryColor = ContextCompat.getColor(
                            requireContext(),
                            org.futo.circles.core.R.color.gray
                        ),
                        secondaryColor = ContextCompat.getColor(
                            requireContext(),
                            org.futo.circles.core.R.color.blue
                        ),
                        dividerColor = ContextCompat.getColor(
                            requireContext(),
                            org.futo.circles.core.R.color.grey_cool_300
                        ),
                        textColor = ContextCompat.getColor(
                            requireContext(),
                            if (context.isNightMode()) org.futo.circles.core.R.color.white
                            else org.futo.circles.core.R.color.black
                        ),
                        textSecondaryColor = ContextCompat.getColor(
                            requireContext(),
                            org.futo.circles.core.R.color.gray
                        )
                    ), RecentEmojisProvider.get(requireContext())
                )
                tearDown()
            }
        }
    }

    private fun onEmojiSelected(emoji: Emoji) {
        emojiPickerListener?.onEmojiSelected(args.roomId, args.eventId, emoji.unicode)
        RecentEmojisProvider.addNewEmoji(requireContext(), emoji)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}