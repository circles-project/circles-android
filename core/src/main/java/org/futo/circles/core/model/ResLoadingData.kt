package org.futo.circles.core.model

import androidx.annotation.StringRes
import org.futo.circles.core.R

sealed class LoadingData(
    open var progress: Int,
    open var total: Int,
    open var isLoading: Boolean
)

data class ResLoadingData(
    @StringRes var messageId: Int = R.string.loading,
    override var progress: Int = 0,
    override var total: Int = 0,
    override var isLoading: Boolean = true
) : LoadingData(progress, total, isLoading)

data class MessageLoadingData(
    var message: String,
    override var progress: Int = 0,
    override var total: Int = 0,
    override var isLoading: Boolean = true
) : LoadingData(progress, total, isLoading)