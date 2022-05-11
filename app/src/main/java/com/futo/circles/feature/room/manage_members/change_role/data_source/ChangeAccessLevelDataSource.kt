package com.futo.circles.feature.room.manage_members.change_role.data_source

import com.futo.circles.model.AccessLevel
import com.futo.circles.model.AccessLevelListItem
import org.matrix.android.sdk.api.session.room.powerlevels.Role

class ChangeAccessLevelDataSource(
    private val levelValue: Int,
    private val myUserLevelValue: Int
) {

    fun getRolesList() =
        AccessLevel.values().filter { myUserLevelValue >= it.levelValue }.map {
            AccessLevelListItem(
                Role.fromValue(it.levelValue, Role.Default.value),
                it.levelValue == levelValue
            )
        }

    fun isValueChanged(newValue: Int) = newValue != levelValue

}