package org.futo.circles.model

import androidx.annotation.StringRes
import org.futo.circles.R

data class LoadingData(
    @StringRes var messageId: Int = R.string.loading,
    var progress: Int = 0,
    var total: Int = 100,
    var isLoading: Boolean = true
)