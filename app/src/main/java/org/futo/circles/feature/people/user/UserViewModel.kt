package org.futo.circles.feature.people.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.collectLatest
import org.futo.circles.extensions.launchUi
import org.futo.circles.model.TimelineListItem

class UserViewModel(
    private val userDataSource: UserDataSource
) : ViewModel() {

    val userLiveData = userDataSource.userLiveData
    val timelineLiveDataLiveData = MutableLiveData<List<TimelineListItem>>()

    init {
        getUsersTimelines()
    }

    private fun getUsersTimelines() {
        launchUi {
            userDataSource.getTimelinesFlow().collectLatest {
                timelineLiveDataLiveData.postValue(it)
            }
        }
    }

}