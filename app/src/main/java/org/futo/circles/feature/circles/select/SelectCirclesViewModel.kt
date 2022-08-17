package org.futo.circles.feature.circles.select

import androidx.lifecycle.ViewModel
import org.futo.circles.model.SelectableRoomListItem

class SelectCirclesViewModel(
    private val dataSource: SelectCirclesDataSource
) : ViewModel() {

    val circlesLiveData = dataSource.circlesLiveData

    fun getSelectedCircles() = dataSource.getSelectedCircles()

    fun onCircleSelected(item: SelectableRoomListItem) {
        dataSource.toggleCircleSelect(item)
    }
}