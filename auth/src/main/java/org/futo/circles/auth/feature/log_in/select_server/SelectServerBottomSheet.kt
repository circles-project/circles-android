package org.futo.circles.auth.feature.log_in.select_server

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.databinding.BottomSheetSelectServerBinding
import org.futo.circles.auth.model.ServerDomainArg
import org.futo.circles.core.base.fragment.TransparentBackgroundBottomSheetDialogFragment
import org.futo.circles.core.extensions.navigateSafe


@AndroidEntryPoint
class SelectServerBottomSheet : TransparentBackgroundBottomSheetDialogFragment() {

    private var binding: BottomSheetSelectServerBinding? = null

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
            findNavController().navigateSafe(
                SelectServerBottomSheetDirections.toSignUpFragment(ServerDomainArg.US)
            )
        }
        binding?.lEuServer?.setOnClickListener {
            findNavController().navigateSafe(
                SelectServerBottomSheetDirections.toSignUpFragment(ServerDomainArg.EU)
            )
        }
        binding?.btnCancel?.setOnClickListener { dismiss() }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}