package org.futo.circles.feature.timeline.post.report

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.R
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.model.ReportCategoryListItem
import org.matrix.android.sdk.api.session.getRoom
import javax.inject.Inject

@ViewModelScoped
class ReportDataSource @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")
    private val eventId: String = savedStateHandle.getOrThrow("eventId")

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