package org.futo.circles.mapping

import org.futo.circles.core.mapping.toCirclesUserSummary
import org.futo.circles.model.PeopleUserListItem
import org.matrix.android.sdk.api.session.user.model.User


fun User.toPeopleUserListItem(isIgnored: Boolean) = PeopleUserListItem(toCirclesUserSummary(), isIgnored)


