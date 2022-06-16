package org.futo.circles.feature.people.user

import androidx.lifecycle.ViewModel

class UserViewModel(
    userDataSource: UserDataSource
) : ViewModel() {

    val userLiveData = userDataSource.userLiveData
    val usersCirclesLiveData = userDataSource.userCirclesLiveData
}