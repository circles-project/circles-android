package com.futo.circles.core.matrix.pass_phrase

import androidx.annotation.StringRes
import com.futo.circles.R
import kotlin.math.roundToInt

data class CreatePassPhraseLoadingData(
    @StringRes var messageId: Int = R.string.generating_recovery_key,
    var progressPercents: Int = 0
) {
    fun setProgress(progress: Int, total: Int) {
        progressPercents = (progress.toFloat() / total.toFloat() * 100).roundToInt()
    }
}