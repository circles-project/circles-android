package org.futo.circles.feature.timeline.post.menu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.fragment.TransparentBackgroundBottomSheetDialogFragment
import org.futo.circles.core.provider.PreferencesProvider
import org.futo.circles.databinding.BottomSheetPostMenuBinding

@AndroidEntryPoint
class PostMenuBottomSheet : TransparentBackgroundBottomSheetDialogFragment() {

    private var binding: BottomSheetPostMenuBinding? = null
    private val args: PostMenuBottomSheetArgs by navArgs()
    private val viewModel by viewModels<PostMenuViewModel>()
    private val preferencesProvider by lazy { PreferencesProvider(requireContext()) }

    private var menuListener: PostMenuListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        menuListener =
            parentFragmentManager.fragments.firstOrNull { it is PostMenuListener } as? PostMenuListener
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetPostMenuBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding?.let {
            with(it) {
                tvDelete.apply {
                    setIsVisible(viewModel.canDeletePost())
                    setOnOptionClickListener(this) { menuListener?.onRemove(args.roomId, args.eventId) }
                }
                tvEdit.apply {
                    setIsVisible(viewModel.canEditPost())
                    setOnOptionClickListener(this) {
                        menuListener?.onEditPostClicked(args.roomId, args.eventId)
                    }
                }
                tvSaveToDevice.apply {
                    setIsVisible(viewModel.isMediaPost())
                    setOnOptionClickListener(this) {
                        viewModel.getPostContent()?.let { menuListener?.onSaveToDevice(it) }
                    }
                }
                tvSaveToGallery.apply {
                    setIsVisible(viewModel.isMediaPost())
                    setOnOptionClickListener(this) { menuListener?.onSaveToGallery(args.roomId, args.eventId) }
                }
                tvReport.apply {
                    setIsVisible(viewModel.isMyPost().not())
                    setOnOptionClickListener(this) { menuListener?.onReport(args.roomId, args.eventId) }
                }
                tvIgnore.apply {
                    setIsVisible(viewModel.isMyPost().not())
                    setOnOptionClickListener(this) {
                        viewModel.getSenderId()?.let { menuListener?.onIgnore(it) }
                    }
                }
                tvEditPoll.apply {
                    setIsVisible(viewModel.canEditPoll())
                    setOnOptionClickListener(this) {
                        menuListener?.onEditPollClicked(args.roomId, args.eventId)
                    }
                }
                tvEndPoll.apply {
                    setIsVisible(viewModel.canEndPoll())
                    setOnOptionClickListener(this) { menuListener?.endPoll(args.roomId, args.eventId) }
                }
                tvInfo.apply {
                    setIsVisible(preferencesProvider.isDeveloperModeEnabled())
                    setOnOptionClickListener(this) { menuListener?.onInfoClicked(args.roomId, args.eventId) }
                }
            }
        }
    }

    private fun setOnOptionClickListener(view: View, lister: () -> Unit) {
        view.setOnClickListener {
            lister.invoke()
            dismiss()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}
