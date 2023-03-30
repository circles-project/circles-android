package org.futo.circles.model

import org.matrix.android.sdk.api.session.securestorage.SsssKeySpec

data class KeyData(
    val recoveryKey: String,
    val keySpec: SsssKeySpec
)