package org.futo.circles.auth.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.nulabinc.zxcvbn.Zxcvbn
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.ViewPasswordStrengthBinding
import org.futo.circles.core.extensions.visible


class PasswordStrengthView(
    context: Context,
    attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {

    private val binding = ViewPasswordStrengthBinding.inflate(LayoutInflater.from(context), this)

    private val strengthCalculator by lazy { Zxcvbn() }
    private var currentScore = 0

    init {
        orientation = VERTICAL
    }

    fun calculateStrength(password: String) {
        visible()
        val strength = strengthCalculator.measure(password)
        setStrengthText(strength.score)
    }

    fun isPasswordStrong() = currentScore >= 2

    private fun setStrengthText(score: Int) {
        currentScore = score
        val colorId = when (score) {
            1 -> {
                binding.tvPasswordStrength.setText(R.string.fair_password)
                org.futo.circles.core.R.color.yellow
            }

            2 -> {
                binding.tvPasswordStrength.setText(R.string.good_password)
                org.futo.circles.core.R.color.orange
            }

            3 -> {
                binding.tvPasswordStrength.setText(R.string.strong_password)
                org.futo.circles.core.R.color.green
            }

            4 -> {
                binding.tvPasswordStrength.setText(R.string.very_strong_password)
                org.futo.circles.core.R.color.teal_700
            }

            else -> {
                binding.tvPasswordStrength.setText(R.string.weak_password)
                org.futo.circles.core.R.color.red
            }
        }
        val color = ContextCompat.getColor(context, colorId)
        binding.passwordProgress.apply {
            progress = score + 1
            progressDrawable?.colorFilter =
                BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                    color, BlendModeCompat.SRC_ATOP
                )
        }
        binding.tvPasswordStrength.setTextColor(color)
    }
}