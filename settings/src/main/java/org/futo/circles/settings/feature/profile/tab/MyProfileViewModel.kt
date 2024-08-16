package org.futo.circles.settings.feature.profile.tab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.select_users.SearchUserDataSource
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.settings.model.PeopleCategoryType
import javax.inject.Inject


@HiltViewModel
class MyProfileViewModel @Inject constructor(
    myProfileDataSource: MyProfileDataSource,
    private val searchUserDataSource: SearchUserDataSource
) : ViewModel() {

    private val session = MatrixSessionProvider.getSessionOrThrow()
    val profileLiveData = session.userService().getUserLive(session.myUserId)

    private val selectedPeopleCategoryFlow = MutableStateFlow(PeopleCategoryType.Following)

    val peopleInfoLiveData = myProfileDataSource.getUsersListByCategoryFlow().asLiveData()
    val peopleLiveData = combine(
        selectedPeopleCategoryFlow,
        myProfileDataSource.getUsersListByCategoryFlow()
    ) { selectedCategory, peopleMap ->
        peopleMap[selectedCategory]?.listData
            ?: throw IllegalArgumentException("Unexpected People category type")
    }.asLiveData()

    init {
        launchBg { searchUserDataSource.loadAllRoomMembersIfNeeded() }
    }

    fun selectPeopleCategory(type: PeopleCategoryType) {
        selectedPeopleCategoryFlow.value = type
    }

}