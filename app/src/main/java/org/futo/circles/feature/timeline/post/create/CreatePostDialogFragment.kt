package org.futo.circles.feature.timeline.post.create

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.showNoInternetConnection
import org.futo.circles.core.feature.picker.helper.MediaPickerHelper
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.MediaType
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.model.TextContent
import org.futo.circles.databinding.DialogFragmentCreatePostBinding
import org.futo.circles.feature.timeline.post.emoji.EmojiPickerListener
import org.futo.circles.model.CreatePostContent

@AndroidEntryPoint
class CreatePostDialogFragment : BottomSheetDialogFragment(), PreviewPostListener,
    EmojiPickerListener {

    private var binding: DialogFragmentCreatePostBinding? = null
    private val args: CreatePostDialogFragmentArgs by navArgs()
    private val viewModel by viewModels<CreatePostViewModel>()

    private val mediaPickerHelper = MediaPickerHelper(this, isVideoAvailable = true)
    private var createPostListener: CreatePostListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        createPostListener =
            parentFragmentManager.fragments.lastOrNull { it is CreatePostListener } as? CreatePostListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentCreatePostBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { setupBottomSheet(it) }
        return dialog
    }

    private fun setupBottomSheet(dialogInterface: DialogInterface) {
        val bottomSheetDialog = dialogInterface as BottomSheetDialog
        val bottomSheet = bottomSheetDialog.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet
        ) ?: return
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
        bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        BottomSheetBehavior.from(bottomSheet).apply {
            peekHeight = resources.displayMetrics.heightPixels
            state = BottomSheetBehavior.STATE_EXPANDED
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED) dismiss()
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            })
        }
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        setToolbarTitle()
        binding?.btnClose?.setOnClickListener { dismiss() }
        binding?.vPostPreview?.setup(this@CreatePostDialogFragment, args.roomId, args.isEdit)
    }

    private fun setupObservers() {
        viewModel.postToEditContentLiveData.observeData(this) {
            when (it.type) {
                PostContentType.IMAGE_CONTENT, PostContentType.VIDEO_CONTENT ->
                    binding?.vPostPreview?.setMediaFromExistingPost(it as MediaContent)

                else -> binding?.vPostPreview?.setText((it as TextContent).message)
            }
        }
    }

    private fun setToolbarTitle() {
        binding?.tvTitle?.text = when {
            args.isEdit -> getString(R.string.edit_post)
            else -> getString(R.string.create_post)
        }
    }

    private fun onMediaSelected(uri: Uri, type: MediaType) {
        binding?.vPostPreview?.setMedia(uri, type)
    }

    private fun sendPost(content: CreatePostContent) {
        if (args.isEdit) onEditPost(content)
        else createPostListener?.onSendPost(
            args.roomId, content, args.eventId
        )
    }

    private fun onEditPost(content: CreatePostContent) {
        val eventId = args.eventId ?: return
        createPostListener?.onEditPost(args.roomId, content, eventId)
    }

    override fun onUploadMediaClicked() {
        mediaPickerHelper.showMediaPickerDialog(
            onImageSelected = { _, uri -> onMediaSelected(uri, MediaType.Image) },
            onVideoSelected = { uri -> onMediaSelected(uri, MediaType.Video) }
        )
    }

    override fun onEmojiClicked() {
        findNavController().navigateSafe(
            CreatePostDialogFragmentDirections.toEmojiBottomSheet(null, null)
        )
    }

    override fun onAddLinkClicked() {
        AddLinkDialog(requireContext()) { title, link ->
            binding?.vPostPreview?.insertLink(title, link)
        }.show()
    }

    override fun onSendClicked(content: CreatePostContent) {
        if (showNoInternetConnection()) return
        sendPost(content)
        dismiss()
    }

    override fun onEmojiSelected(roomId: String?, eventId: String?, emoji: String) {
        binding?.vPostPreview?.insertEmoji(emoji)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}