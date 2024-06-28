package org.futo.circles.auth.feature.log_in.suggestion

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.BottomSheetLoginSuggestionBinding
import org.futo.circles.core.base.fragment.TransparentBackgroundBottomSheetDialogFragment

@AndroidEntryPoint
class LoginSuggestionBottomSheet : TransparentBackgroundBottomSheetDialogFragment() {

    private var binding: BottomSheetLoginSuggestionBinding? = null
    private val args: LoginSuggestionBottomSheetArgs by navArgs()
    private var applySuggestionListener: LoginSuggestionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        applySuggestionListener =
            parentFragmentManager.fragments.lastOrNull { it is LoginSuggestionListener } as? LoginSuggestionListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetLoginSuggestionBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding?.let {
            with(it) {
                tvTitle.text = getString(R.string.did_you_mean_format, args.userId)
                btnOk.apply {
                    text = getString(R.string.log_in_as_format, args.userId)
                    setOnClickListener {
                        applySuggestionListener?.onLoginSuggestionApplied(
                            args.userId,
                            args.isForgotPassword
                        )
                        dismiss()
                    }
                    btnCancel.setOnClickListener { dismiss() }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}