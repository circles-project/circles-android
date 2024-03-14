package org.futo.circles.core.feature.circles.filter

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import javax.inject.Inject

@HiltViewModel
class FilterTimelinesViewModel @Inject constructor(
    private val dataSource: FilterTimelinesDataSource
) : ViewModel() {

    val circleInfoLiveData = dataSource.circleSummaryLiveData
    val timelinesLiveData = dataSource.timelinesLiveData
    val updateFilterResultLiveData = SingleEventLiveData<Response<Unit?>>()

    fun saveFilter() {
        launchBg {
            val result = dataSource.applyFilter()
            updateFilterResultLiveData.postValue(result)
        }
    }

    fun toggleItemSelected(id: String) {
        dataSource.toggleItemSelected(id)
    }

    fun selectAllItems() {
        dataSource.selectAllTimelines()
    }

}