package org.futo.circles.feature.timeline.poll

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.navArgs
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentCreatePollBinding
import org.futo.circles.extensions.getText
import org.futo.circles.extensions.onBackPressed
import org.futo.circles.model.CreatePollContent
import org.matrix.android.sdk.api.session.room.model.message.PollType

class CreatePollDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentCreatePollBinding::inflate) {

    private val args: CreatePollDialogFragmentArgs by navArgs()
    private val binding by lazy {
        getBinding() as DialogFragmentCreatePollBinding
    }

    private var createPollListener: CreatePollListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        createPollListener =
            parentFragmentManager.fragments.firstOrNull { it is CreatePollListener } as? CreatePollListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        with(binding) {
            btnCreate.setOnClickListener {
                createPoll()
                onBackPressed()
            }
            lvPostOptions.setOnChangeListener { handleCreateButtonAvailable() }
            tilQuestion.editText?.doAfterTextChanged { handleCreateButtonAvailable() }
            btnAddOption.setOnClickListener {
                lvPostOptions.addOption()
                lvPostOptions.post { scrollView.fullScroll(View.FOCUS_DOWN) }
            }
        }
    }

    private fun handleCreateButtonAvailable() {
        binding.btnCreate.isEnabled =
            binding.lvPostOptions.isValidInput() && binding.tilQuestion.getText().isNotEmpty()
    }

    private fun createPoll() {
        createPollListener?.onCreatePoll(
            args.roomId,
            CreatePollContent(
                if (binding.btnOpenPoll.isChecked) PollType.DISCLOSED else PollType.UNDISCLOSED,
                binding.tilQuestion.getText(),
                binding.lvPostOptions.getOptionsList()
            )
        )
    }

}