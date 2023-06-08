package org.futo.circles.feature.room.manage_members.change_role

import androidx.lifecycle.SavedStateHandle
import dagger.assisted.AssistedFactory
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.base.READ_ONLY_ROLE
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.model.AccessLevel
import org.futo.circles.model.AccessLevelListItem
import org.matrix.android.sdk.api.session.room.powerlevels.Role
import javax.inject.Inject

@ViewModelScoped
class ChangeAccessLevelDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle
) {

    private val levelValue: Int = savedStateHandle.getOrThrow("levelValue")
    private val myUserLevelValue: Int = savedStateHandle.getOrThrow("myUserLevelValue")

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