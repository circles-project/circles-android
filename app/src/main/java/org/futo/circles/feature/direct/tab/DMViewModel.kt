package org.futo.circles.feature.direct.tab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DMViewModel @Inject constructor(
    dataSource: DMDataSource
) : ViewModel() {

    val dmsLiveData = dataSource.getDirectMessagesListFlow().asLiveData()

}