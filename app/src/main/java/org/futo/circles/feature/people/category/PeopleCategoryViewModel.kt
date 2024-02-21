package org.futo.circles.feature.people.category

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.feature.user.UserOptionsDataSource
import org.futo.circles.model.PeopleCategoryTypeArg
import javax.inject.Inject

@HiltViewModel
class PeopleCategoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    peopleCategoryDataSource: PeopleCategoryDataSource,
    private val userOptionsDataSource: UserOptionsDataSource
) : ViewModel() {

    private val categoryType: PeopleCategoryTypeArg = savedStateHandle.getOrThrow("categoryType")
    val usersLiveData =
        peopleCategoryDataSource.getUsersListByCategoryFlow(categoryType).asLiveData()

    val unIgnoreUserLiveData = SingleEventLiveData<Response<Unit?>>()

    fun unIgnoreUser(userId: String) {
        launchBg {
            val result = userOptionsDataSource.unIgnoreSender(userId)
            unIgnoreUserLiveData.postValue(result)
        }
    }

}