package org.futo.circles.feature.direct.timeline.menu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.base.fragment.TransparentBackgroundBottomSheetDialogFragment
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.model.TextContent
import org.futo.circles.core.provider.PreferencesProvider
import org.futo.circles.databinding.BottomSheetDmMenuBinding
import org.futo.circles.feature.direct.timeline.listeners.DmOptionsListener
import org.futo.circles.feature.timeline.post.menu.PostMenuViewModel

@AndroidEntryPoint
class DmMenuBottomSheet : TransparentBackgroundBottomSheetDialogFragment() {

    private var binding: BottomSheetDmMenuBinding? = null
    private val args: DmMenuBottomSheetArgs by navArgs()
    private val viewModel by viewModels<PostMenuViewModel>()
    private val preferencesProvider by lazy { PreferencesProvider(requireContext()) }

    private var menuListener: DmOptionsListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        menuListener =
            parentFragmentManager.fragments.firstOrNull { it is DmOptionsListener } as? DmOptionsListener
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetDmMenuBinding.inflate(inflater, container, false)
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
                    setIsVisible(viewModel.isMyPost())
                    setOnClickListener {
                        menuListener?.onRemove(args.eventId)
                        dismiss()
                    }
                }
                tvEdit.apply {
                    setIsVisible(
                        viewModel.isMyPost() && viewModel.getPostContent()?.isText() == true
                    )
                    setOnClickListener {
                        (viewModel.getPostContent() as? TextContent)?.let {
                            menuListener?.onEditActionClicked(args.eventId, it.message)
                            dismiss()
                        }
                    }
                }
                tvShare.apply {
                    setOnClickListener {
                        viewModel.getPostContent()?.let {
                            menuListener?.onShare(it)
                            dismiss()
                        }
                    }
                }
                tvSaveToDevice.apply {
                    setIsVisible(viewModel.isMediaPost())
                    setOnClickListener {
                        viewModel.getPostContent()?.let { menuListener?.onSaveToDevice(it) }
                        dismiss()
                    }
                }
                tvReact.apply {
                    setOnClickListener {
                        findNavController().navigateSafe(
                            DmMenuBottomSheetDirections.toEmojiBottomSheet(
                                args.roomId,
                                args.eventId
                            )
                        )
                    }
                }
                tvReply.apply {
                    setIsVisible(
                        viewModel.isMyPost().not() && viewModel.getPostContent()?.isText() == true
                    )
                    setOnClickListener {
                        (viewModel.getPostContent() as? TextContent)?.let {
                            menuListener?.onReply(it.message)
                            dismiss()
                        }
                    }
                }
                tvInfo.apply {
                    setIsVisible(preferencesProvider.isDeveloperModeEnabled())
                    setOnClickListener {
                        findNavController().navigateSafe(
                            DmMenuBottomSheetDirections.toPostInfoDialogFragment(
                                args.roomId,
                                args.eventId
                            )
                        )
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
