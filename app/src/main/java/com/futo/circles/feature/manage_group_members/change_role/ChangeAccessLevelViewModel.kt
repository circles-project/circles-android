package com.futo.circles.feature.manage_group_members.change_role

import androidx.lifecycle.ViewModel
import com.futo.circles.feature.manage_group_members.change_role.data_source.ChangeAccessLevelDataSource
import com.futo.circles.model.AccessLevelListItem

class ChangeAccessLevelViewModel(
    private val dataSource: ChangeAccessLevelDataSource
) : ViewModel() {

    fun toggleAccessLevel(item: AccessLevelListItem) {

    }
}