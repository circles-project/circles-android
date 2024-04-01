package org.futo.circles.feature.explanation

import android.app.ActionBar
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Space
import androidx.appcompat.app.AppCompatDialog
import org.futo.circles.R
import org.futo.circles.auth.databinding.DialogCirclesExplanationBinding
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.CircleRoomTypeArg.Group
import org.futo.circles.core.provider.PreferencesProvider


class CirclesExplanationDialog(context: Context, private val roomType: CircleRoomTypeArg) :
    AppCompatDialog(context) {

    private val binding = DialogCirclesExplanationBinding.inflate(LayoutInflater.from(context))
    private val preferencesProvider by lazy { PreferencesProvider(context) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCancelable(false)
        window?.apply {
            setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 20))
            setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT)
        }
        setupViews()
        preferencesProvider.setShouldShowExplanation(false, roomType)
    }

    private fun setupViews() {
        with(binding) {
            btnDone.setOnClickListener { this@CirclesExplanationDialog.dismiss() }
            tvDescription.movementMethod = ScrollingMovementMethod()
            when (roomType) {
                Group -> {
                    tvTitle.setText(R.string.groups)
                    tvDescription.text = Html.fromHtml(
                        context.getString(R.string.group_explanation),
                        Html.FROM_HTML_MODE_COMPACT
                    )
                    setupImages(listOf(R.drawable.explanation_groups))
                }

                else -> {
                    tvTitle.setText(R.string.circles)
                    tvDescription.text = Html.fromHtml(
                        context.getString(R.string.circle_explanation),
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