package org.futo.circles.feature.timeline.poll

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentCreatePollBinding
import org.futo.circles.extensions.onBackPressed
import org.futo.circles.model.CreatePollContent

interface CreatePollListener {
    fun onCreatePoll(roomId: String, pollContent: CreatePollContent)
}

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
            toolbar.setNavigationOnClickListener { onBackPressed() }
            btnCreate.setOnClickListener {
                createPoll()
                onBackPressed()
            }
        }
    }

    private fun createPoll() {

    }

}