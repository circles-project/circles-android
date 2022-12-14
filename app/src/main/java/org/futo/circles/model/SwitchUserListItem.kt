package org.futo.circles.model

import org.futo.circles.core.list.IdEntity
import org.matrix.android.sdk.api.auth.data.SessionParams
import org.matrix.android.sdk.api.auth.data.sessionId

data class SwitchUserListItem(
    val params: SessionParams
) : IdEntity<String> {
    override val id: String = params.credentials.sessionId()
}