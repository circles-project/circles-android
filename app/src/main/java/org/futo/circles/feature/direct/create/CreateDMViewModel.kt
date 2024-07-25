package org.futo.circles.feature.direct.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.extensions.launchUi
import org.futo.circles.core.model.CirclesUserSummary
import org.futo.circles.core.provider.MatrixSessionProvider
import javax.inject.Inject

@HiltViewModel
class CreateDMViewModel @Inject constructor(
    private val createDMDataSource: CreateDMDataSource
) : ViewModel() {

    val searchUsersLiveData = MutableLiveData<List<CirclesUserSummary>>()
    val inviteForDirectMessagesLiveData = SingleEventLiveData<Response<String>>()

    init {
        launchBg { createDMDataSource.refreshRoomMembers() }
    }

    fun initSearchListener(queryFlow: StateFlow<String>) {
        launchUi {
            queryFlow
                .flatMapLatest { query -> createDMDataSource.search(query) }
                .collectLatest { items -> searchUsersLiveData.postValue(items) }
        }
    }

    fun inviteForDirectMessages(userId: String) {
        launchBg {
            val result = createResult {
                MatrixSessionProvider.getSessionOrThrow().roomService().createDirectRoom(userId)
            }
            inviteForDirectMessagesLiveData.postValue(result)
        }
    }

}