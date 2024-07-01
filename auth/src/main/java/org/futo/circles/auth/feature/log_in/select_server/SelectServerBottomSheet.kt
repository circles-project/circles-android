package org.futo.circles.auth.feature.log_in.select_server

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.databinding.BottomSheetSelectServerBinding
import org.futo.circles.auth.feature.sign_up.SignupSelectDomainListener
import org.futo.circles.core.base.CirclesAppConfig
import org.futo.circles.core.base.fragment.TransparentBackgroundBottomSheetDialogFragment


@AndroidEntryPoint
class SelectServerBottomSheet : TransparentBackgroundBottomSheetDialogFragment() {

    private var binding: BottomSheetSelectServerBinding? = null

    private var signupSelectDomainListener: SignupSelectDomainListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        signupSelectDomainListener =
            parentFragmentManager.fragments.lastOrNull { it is SignupSelectDomainListener } as? SignupSelectDomainListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetSelectServerBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding?.lUsServer?.setOnClickListener {
            signupSelectDomainListener?.onSignupDomainSelected(CirclesAppConfig.usDomain)
            dismiss()
        }
        binding?.lEuServer?.setOnClickListener {
            signupSelectDomainListener?.onSignupDomainSelected(CirclesAppConfig.euDomain)
            dismiss()
        }
        binding?.btnCancel?.setOnClickListener { dismiss() }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}