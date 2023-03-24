package org.futo.circles.feature.settings.active_sessions.bootstrap

import org.matrix.android.sdk.api.auth.UserInteractiveAuthInterceptor
import org.matrix.android.sdk.api.session.securestorage.SsssKeySpec


data class BootstrapCrossSigningParams(
    val userInteractiveAuthInterceptor: UserInteractiveAuthInterceptor? = null,
    val progressListener: BootstrapProgressListener? = null,
    val passphrase: String?,
    val keySpec: SsssKeySpec? = null
)