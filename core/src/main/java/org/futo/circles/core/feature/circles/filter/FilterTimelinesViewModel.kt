package org.futo.circles.core.feature.circles.filter

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilterTimelinesViewModel @Inject constructor(
    private val dataSource: FilterTimelinesDataSource
) : ViewModel() {

}