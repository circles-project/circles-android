package com.futo.circles.feature.group_members.change_role

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.feature.group_members.change_role.data_source.ChangeAccessLevelDataSource

class ChangeAccessLevelViewModel(
    private val dataSource: ChangeAccessLevelDataSource
) : ViewModel() {

    val accessLevelsLiveData = MutableLiveData(dataSource.getRolesList())
    val isLevelChangedLiveData = MutableLiveData(false)

    fun toggleAccessLevel(newLevelValue: Int) {
        val list = accessLevelsLiveData.value?.toMutableList()?.map { item ->
            item.copy(isSelected = item.role.value == newLevelValue)
        }
        accessLevelsLiveData.postValue(list)
        isLevelChangedLiveData.postValue(dataSource.isValueChanged(newLevelValue))
    }

    fun getCurrentSelectedValue() =
        accessLevelsLiveData.value?.firstOrNull { it.isSelected }?.role?.value
}