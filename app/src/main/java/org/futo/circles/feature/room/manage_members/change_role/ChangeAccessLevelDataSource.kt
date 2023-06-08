package org.futo.circles.feature.room.manage_members.change_role

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.futo.circles.base.READ_ONLY_ROLE
import org.futo.circles.model.AccessLevel
import org.futo.circles.model.AccessLevelListItem
import org.matrix.android.sdk.api.session.room.powerlevels.Role

class ChangeAccessLevelDataSource @AssistedInject constructor(
    @Assisted private val levelValue: Int,
    @Assisted private val myUserLevelValue: Int
) {

    @AssistedFactory
    interface Factory {
        fun create(levelValue: Int, myUserLevelValue: Int): ChangeAccessLevelDataSource
    }

    fun getRolesList() = AccessLevel.values().map {
        AccessLevelListItem(
            Role.fromValue(it.levelValue, Role.Default.value),
            it.levelValue == levelValue
        )
    }.toMutableList().apply {
        add(AccessLevelListItem(Role.Custom(READ_ONLY_ROLE), READ_ONLY_ROLE == levelValue))
    }.filter { myUserLevelValue >= it.role.value }

    fun isValueChanged(newValue: Int) = newValue != levelValue

}