package org.futo.circles.core.feature.room.manage_members.change_role

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.model.AccessLevel
import org.futo.circles.core.model.AccessLevelListItem
import org.matrix.android.sdk.api.session.room.powerlevels.Role
import javax.inject.Inject

@ViewModelScoped
class ChangeAccessLevelDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle
) {

    private val levelValue: Int = savedStateHandle.getOrThrow("levelValue")
    private val myUserLevelValue: Int = savedStateHandle.getOrThrow("myUserLevelValue")

    fun getRolesList() = AccessLevel.entries.map {
        AccessLevelListItem(
            Role.fromValue(it.levelValue, Role.Default.value),
            it.levelValue == levelValue
        )
    }.toMutableList().filter { myUserLevelValue >= it.role.value }

    fun isValueChanged(newValue: Int) = newValue != levelValue

}