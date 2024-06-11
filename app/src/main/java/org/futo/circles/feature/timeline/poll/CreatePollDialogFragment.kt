package org.futo.circles.feature.timeline.poll

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.model.CreatePollContent
import org.futo.circles.databinding.DialogFragmentCreatePollBinding
import org.futo.circles.feature.timeline.post.create.PostSentListener
import org.matrix.android.sdk.api.session.room.model.message.PollType

@AndroidEntryPoint
class CreatePollDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentCreatePollBinding>(DialogFragmentCreatePollBinding::inflate),
    HasLoadingState {

    private val args: CreatePollDialogFragmentArgs by navArgs()
    private val isEdit by lazy { args.eventId != null }

    override val fragment: Fragment
        get() = this

    private val viewModel by viewModels<CreatePollViewModel>()

    private var sentPostListener: PostSentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sentPostListener =
            parentFragmentManager.fragments.lastOrNull { it is PostSentListener } as? PostSentListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            toolbar.title = getString(if (isEdit) R.string.edit_poll else R.string.create_poll)
            btnCreate.apply {
                setText(getString(if (isEdit) R.string.edit else org.futo.circles.core.R.string.create))
                setOnClickListener {
                    createPoll()
                    startLoading(binding.btnCreate)
                }
            }
            lvPostOptions.setOnChangeListener { handleCreateButtonAvailable() }
            tilQuestion.editText?.doAfterTextChanged { handleCreateButtonAvailable() }
            btnAddOption.setOnClickListener {
                lvPostOptions.addOption()
                lvPostOptions.post { scrollView.fullScroll(View.FOCUS_DOWN) }
            }
            if (!isEdit) repeat(2) { lvPostOptions.addOption() }
        }
    }

    private fun setupObservers() {
        viewModel.pollToEditLiveData.observeData(this) { content ->
            with(binding) {
                tilQuestion.editText?.setText(content.question)
                content.options.forEach { option ->
                    lvPostOptions.addOption(option.optionAnswer)
                }
                binding.btnClosedPoll.isChecked = content.isClosedType
                handleCreateButtonAvailable()
            }
        }
        viewModel.sendLiveData.observeData(this) { sendStateLiveData ->
            sendStateLiveData.observeData(this) { sendState ->
                if (sendState.isSent()) {
                    if (!isEdit) sentPostListener?.onPostSent()
                    stopLoading()
                    onBackPressed()
                } else if (sendState.hasFailed()) {
                    stopLoading()
                    showError(getString(R.string.failed_to_send))
                }
            }
        }
    }

    private fun handleCreateButtonAvailable() {
        binding.btnCreate.isEnabled =
            binding.lvPostOptions.isValidInput() && binding.tilQuestion.getText().isNotEmpty()
    }

    private fun createPoll() {
        val pollContent = CreatePollContent(
            if (binding.btnOpenPoll.isChecked) PollType.DISCLOSED else PollType.UNDISCLOSED,
            binding.tilQuestion.getText(),
            binding.lvPostOptions.getOptionsList()
        )
        viewModel.onSendPoll(pollContent)
    }

}