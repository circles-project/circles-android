package org.futo.circles.core.model

import androidx.annotation.StringRes
import org.futo.circles.core.R

data class LoadingData(
    @StringRes var messageId: Int = R.string.loading,
    var progress: Int = 0,
    var total: Int = 0,
    var isLoading: Boolean = true
)