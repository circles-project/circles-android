package com.futo.circles.core.matrix.pass_phrase

import androidx.annotation.StringRes
import com.futo.circles.R

data class CreatePassPhraseLoadingData(
    @StringRes var messageId: Int = R.string.generating_recovery_key,
    var progress: Int = 0,
    var total: Int = 100,
    var isLoading: Boolean = true
)