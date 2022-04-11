package com.futo.circles.core.matrix.pass_phrase

import androidx.annotation.StringRes
import com.futo.circles.R

data class PassPhraseLoadingData(
    @StringRes var messageId: Int = R.string.loading,
    var progress: Int = 0,
    var total: Int = 100,
    var isLoading: Boolean = true
)