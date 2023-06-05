package org.futo.circles.feature.timeline.post.report

import android.content.Context
import androidx.lifecycle.MutableLiveData
import org.futo.circles.R
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.model.ReportCategoryListItem
import org.matrix.android.sdk.api.session.getRoom

class ReportDataSource(
    roomId: String,
    private val eventId: String,
    private val context: Context
) {

    private val room = MatrixSessionProvider.currentSession?.getRoom(roomId)
    val reportCategoriesLiveData = MutableLiveData(getInitialReportCategories())

    suspend fun report(score: Int) = createResult {
        room?.reportingService()?.reportContent(eventId, score, getSelectedCategoryName())
    }

    private fun getInitialReportCategories(): List<ReportCategoryListItem> =
        context.resources.getStringArray(R.array.report_categories).mapIndexed { i, name ->
            ReportCategoryListItem(id = i, name = name)
        }

    fun toggleReportCategory(id: Int) {
        val list = reportCategoriesLiveData.value?.toMutableList()?.map {
            it.copy(isSelected = it.id == id)
        }
        reportCategoriesLiveData.postValue(list)
    }

    private fun getSelectedCategoryName(): String =
        reportCategoriesLiveData.value?.firstOrNull { it.isSelected }?.name ?: ""

}