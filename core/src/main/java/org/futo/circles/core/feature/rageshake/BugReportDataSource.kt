package org.futo.circles.core.feature.rageshake

import okhttp3.ResponseBody
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.feature.rageshake.io.BugreportApiService
import javax.inject.Inject

class BugReportDataSource @Inject constructor(
    private val bugReportDataCollector: BugReportDataCollector,
    private val bugreportApiService: BugreportApiService
) {

    fun getScreenShot() = bugReportDataCollector.screenshot

    suspend fun sendReport(
        description: String,
        contactInfo: String,
        sendLogs: Boolean,
        sendScreenshot: Boolean
    ): Response<ResponseBody> {
        val data =
            bugReportDataCollector.collectData(description, contactInfo, sendLogs, sendScreenshot)
        return createResult {
            bugreportApiService.sendBugReport(getRageShakeUrl(), data)
        }
    }
}