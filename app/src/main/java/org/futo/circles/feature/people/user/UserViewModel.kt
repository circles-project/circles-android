package org.futo.circles.feature.people.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

class UserViewModel(
    userDataSource: UserDataSource
) : ViewModel() {

    val userLiveData = userDataSource.userLiveData
    val timelineLiveDataLiveData = userDataSource.getTimelinesFlow().asLiveData()
}