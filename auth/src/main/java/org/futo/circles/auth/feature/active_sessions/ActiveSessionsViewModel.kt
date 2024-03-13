package org.futo.circles.auth.feature.active_sessions

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.auth.model.ActiveSession
import org.futo.circles.auth.model.ActiveSessionListItem
import org.futo.circles.auth.model.SessionHeader
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import javax.inject.Inject

@HiltViewModel
class ActiveSessionsViewModel @Inject constructor(
    private val dataSource: ActiveSessionsDataSource
) : ViewModel() {

    val removeSessionLiveData = SingleEventLiveData<Response<Unit?>>()
    val resetKeysLiveData = SingleEventLiveData<Response<Unit?>>()
    val startReAuthEventLiveData = dataSource.startReAuthEventLiveData

    private val loadingItemsIdsList = MutableLiveData<Set<String>>(emptySet())

    val activeSessionsLiveData = MediatorLiveData<List<ActiveSessionListItem>>().also {
        it.addSource(loadingItemsIdsList) { loadingItemsValue ->
            val currentList = it.value ?: emptyList()
            it.postValue(
                currentList.map { item ->
                    when (item) {
                        is ActiveSession -> item.copy(isLoading = loadingItemsValue.contains(item.id))
                        is SessionHeader -> item
                    }
                }
            )
        }
        it.addSource(dataSource.getActiveSessionsFlow().asLiveData()) { value ->
            it.postValue(value)
        }
    }


    fun onSessionClicked(deviceId: String) {
        dataSource.toggleOptionsVisibilityFor(deviceId)
    }

    fun removeSession(deviceId: String) {
        launchBg {
            toggleItemLoading(deviceId)
            val deactivateResult = dataSource.removeSession(deviceId)
            toggleItemLoading(deviceId)
            removeSessionLiveData.postValue(deactivateResult)
        }
    }

    fun resetKeysToEnableCrossSigning() {
        launchBg {
            val resetKeysResult = dataSource.resetKeysToEnableCrossSigning()
            resetKeysLiveData.postValue(resetKeysResult)
        }
    }

    private fun toggleItemLoading(id: String) {
        val currentSet = loadingItemsIdsList.value?.toMutableSet() ?: return
        val newLoadingSet = currentSet.apply {
            if (this.contains(id)) remove(id)
            else add(id)
        }
        loadingItemsIdsList.postValue(newLoadingSet)
    }
}