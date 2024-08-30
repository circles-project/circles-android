package org.futo.circles.feature.timeline.post.menu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.base.fragment.TransparentBackgroundBottomSheetDialogFragment
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.provider.PreferencesProvider
import org.futo.circles.databinding.BottomSheetPostMenuBinding

@AndroidEntryPoint
class PostMenuBottomSheet : TransparentBackgroundBottomSheetDialogFragment() {

    private var binding: BottomSheetPostMenuBinding? = null
    private val args: PostMenuBottomSheetArgs by navArgs()
    private val viewModel by viewModels<PostMenuViewModel>()
    private val preferencesProvider by lazy { PreferencesProvider(requireContext()) }
    private val navigator by lazy { PostMenuBottomSheetNavigator(this) }

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
                    setOnClickListener {
                        menuListener?.onRemove(args.roomId, args.eventId)
                        dismiss()
                    }
                }
                tvShare.apply {
                    setIsVisible(!viewModel.isPoll())
                    setOnClickListener {
                        viewModel.getPostContent()?.let { menuListener?.onShare(it) }
                        dismiss()
                    }
                }
                tvEdit.apply {
                    setIsVisible(viewModel.canEditPost())
                    setOnClickListener {
                        navigator.navigateToEditPost(args.roomId, args.eventId)
                    }
                }
                tvSaveToDevice.apply {
                    setIsVisible(viewModel.isMediaPost())
                    setOnClickListener {
                        viewModel.getPostContent()?.let { menuListener?.onSaveToDevice(it) }
                        dismiss()
                    }
                }
                tvReport.apply {
                    setIsVisible(viewModel.isMyPost().not())
                    setOnClickListener {
                        navigator.navigateToReport(args.roomId, args.eventId)
                    }
                }
                tvIgnore.apply {
                    setIsVisible(viewModel.isMyPost().not())
                    setOnClickListener {
                        viewModel.getSenderId()?.let { menuListener?.onIgnore(it) }
                        dismiss()
                    }
                }
                tvEditPoll.apply {
                    setIsVisible(viewModel.canEditPoll())
                    setOnClickListener {
                        navigator.navigateToEditPoll(args.roomId, args.eventId)
                    }
                }
                tvEndPoll.apply {
                    setIsVisible(viewModel.canEndPoll())
                    setOnClickListener {
                        menuListener?.endPoll(args.roomId, args.eventId)
                        dismiss()
                    }
                }
                tvInfo.apply {
                    setIsVisible(preferencesProvider.isDeveloperModeEnabled())
                    setOnClickListener {
                        navigator.navigateToInfo(args.roomId, args.eventId)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}
