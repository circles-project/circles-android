package org.futo.circles.auth.model

import org.futo.circles.core.list.IdEntity
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.user.model.User

data class SwitchUserListItem(
    override val id: String,
    val session: Session,
    val user: User
) : org.futo.circles.core.list.IdEntity<String>