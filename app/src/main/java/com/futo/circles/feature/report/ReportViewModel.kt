package com.futo.circles.feature.report

import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.report.data_source.ReportDataSource

class ReportViewModel(
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