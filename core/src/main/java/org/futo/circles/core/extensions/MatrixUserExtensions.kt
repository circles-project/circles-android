package org.futo.circles.core.extensions

import org.futo.circles.core.utils.UserUtils
import org.matrix.android.sdk.api.session.user.model.User


fun User.notEmptyDisplayName(): String = getName(userId, displayName)

private fun getName(userId: String, displayName: String?): String {
    val name = displayName?.takeIf { it.isNotEmpty() }
        ?: userId.replace("@", "").substringBefore(":")
    return UserUtils.removeDomainSuffix(name)
}