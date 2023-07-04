package org.futo.circles.feature.timeline.post.report

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportDataSource: ReportDataSource
) : ViewModel() {

    val reportLiveData = SingleEventLiveData<Response<Unit?>>()
    val reportCategoriesLiveData = reportDataSource.reportCategoriesLiveData

    fun report(score: Int) {
        launchBg { reportLiveData.postValue(reportDataSource.report(score)) }
    }

    fun toggleReportCategory(categoryId: Int) {
        reportDataSource.toggleReportCategory(categoryId)
    }
}