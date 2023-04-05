package org.futo.circles.feature.rageshake

import okhttp3.ResponseBody
import org.futo.circles.core.utils.ConfigUtils
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.io.BugreportApiService

class BugReportDataSource(
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
            bugreportApiService.sendBugReport(ConfigUtils.getRageshakeUrl(), data)
        }
    }
}