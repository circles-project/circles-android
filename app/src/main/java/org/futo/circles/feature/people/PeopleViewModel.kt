package org.futo.circles.feature.people

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.extensions.launchUi
import org.futo.circles.core.feature.workspace.SharedCircleDataSource
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.model.PeopleListItem
import javax.inject.Inject

@HiltViewModel
class PeopleViewModel @Inject constructor(
    private val peopleDataSource: PeopleDataSource,
    private val sharedCircleDataSource: SharedCircleDataSource
) : ViewModel() {

    private val session = MatrixSessionProvider.getSessionOrThrow()
    val peopleLiveData = MutableLiveData<List<PeopleListItem>>()
    val profileLiveData = session.userService().getUserLive(session.myUserId)

    init {
        launchBg { peopleDataSource.refreshRoomMembers() }
    }

    fun initSearchListener(queryFlow: StateFlow<String>) {
        launchUi {
            queryFlow
                .flatMapLatest { query -> peopleDataSource.getPeopleList(query) }
                .collectLatest { items -> peopleLiveData.postValue(items) }
        }
    }

    fun getSharedCircleSpaceId(): String? = sharedCircleDataSource.getSharedCirclesSpaceId()
}