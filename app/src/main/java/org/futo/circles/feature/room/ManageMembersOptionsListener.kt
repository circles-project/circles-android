package org.futo.circles.feature.room

import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent

interface ManageMembersOptionsListener {
    fun onSetAccessLevel(userId: String, powerLevelsContent: PowerLevelsContent)
    fun onRemoveUser(userId: String)
    fun onBanUser(userId: String)
    fun unBanUser(userId: String)
    fun cancelPendingInvitation(userId: String)
}