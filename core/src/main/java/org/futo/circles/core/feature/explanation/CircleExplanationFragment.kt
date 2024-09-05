package org.futo.circles.core.feature.explanation

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.base.fragment.BaseBindingFragment
import org.futo.circles.core.databinding.FragmentCircleExplanationBinding
import org.futo.circles.core.extensions.navigateSafe


@AndroidEntryPoint
class CircleExplanationFragment :
    BaseBindingFragment<FragmentCircleExplanationBinding>(FragmentCircleExplanationBinding::inflate) {

    private var popUpListener: ExplanationDismissListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        popUpListener = (parentFragment as? ExplanationDismissListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.default_background)
        setupViews()
    }

    private fun setupViews() {
        with(binding) {
            btnNext.apply {
                popUpListener?.let { listener ->
                    setText(R.string.got_it)
                    setOnClickListener {
                        listener.onDismissPopUp()
                    }
                } ?: run {
                    setText(R.string.next)
                    setOnClickListener {
                        findNavController().navigateSafe(
                            Uri.parse("circles://auth/setupCircles")
                        )
                    }
                }
            }
            tvDescription.movementMethod = ScrollingMovementMethod()
            tvTitle.setText(R.string.circles)
            tvDescription.text = Html.fromHtml(
                getString(R.string.circle_explanation),
                Html.FROM_HTML_MODE_COMPACT
            )
        }
    }
}
