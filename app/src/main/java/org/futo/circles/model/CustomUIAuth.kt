package org.futo.circles.model

import org.futo.circles.core.auth.BaseLoginStagesDataSource
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.auth.UIABaseAuth
import org.matrix.android.sdk.api.util.JsonDict

data class CustomUIAuth(
    override val session: String,
    val auth: JsonDict
) : UIABaseAuth {
    override fun hasAuthInfo() = true

    override fun copyWithSession(session: String) = this.copy(session = session)

    override fun asMap(): Map<String, *> = auth.toMutableMap().apply {
        this["session"] = session
        this[BaseLoginStagesDataSource.USER_PARAM_KEY] =
            MatrixSessionProvider.currentSession?.myUserId ?: ""
    }

}
