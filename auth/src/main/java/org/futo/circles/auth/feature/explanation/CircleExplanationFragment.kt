package org.futo.circles.auth.feature.explanation

import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Space
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.FragmentCircleExplanationBinding
import org.futo.circles.core.base.fragment.BaseBindingFragment
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.model.CircleRoomTypeArg


@AndroidEntryPoint
class CircleExplanationFragment :
    BaseBindingFragment<FragmentCircleExplanationBinding>(FragmentCircleExplanationBinding::inflate) {

    private val args: CircleExplanationFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        with(binding) {
            btnNext.setOnClickListener {
                findNavController().navigateSafe(
                    CircleExplanationFragmentDirections.toSetupCirclesFragment()
                )
            }
            tvDescription.movementMethod = ScrollingMovementMethod()
            when (args.roomType) {
                CircleRoomTypeArg.Group -> {
                    tvTitle.setText(R.string.groups)
                    tvDescription.text = Html.fromHtml(
                        getString(R.string.group_explanation),
                        Html.FROM_HTML_MODE_COMPACT
                    )
                    setupImages(listOf(R.drawable.explanation_groups))
                }

                else -> {
                    tvTitle.setText(R.string.circles)
                    tvDescription.text = Html.fromHtml(
                        getString(R.string.circle_explanation),
                        Html.FROM_HTML_MODE_COMPACT
                    )
                    setupImages(
                        listOf(
                            R.drawable.explanation_circles_1,
                            R.drawable.explanation_circles_2,
                            R.drawable.explanation_circles_3,
                            R.drawable.explanation_circles_4
                        )
                    )
                }
            }
        }
    }

    private fun setupImages(images: List<Int>) {
        if (images.isEmpty()) return
        val spaceWeight = if (images.size == 1) 0f else images.size.toFloat() / (images.size - 1)
        val imageWeight = (100f - images.size * spaceWeight) / images.size.toFloat()
        images.forEachIndexed { index, i ->
            binding.lImages.addView(ImageView(context).apply {
                adjustViewBounds = true
                layoutParams =
                    LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                        weight = imageWeight
                    }
                setImageResource(i)
            })
            if (index != images.size - 1) {
                binding.lImages.addView(Space(context).apply {
                    layoutParams =
                        LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                            weight = spaceWeight
                        }
                })
            }
        }
    }

}
