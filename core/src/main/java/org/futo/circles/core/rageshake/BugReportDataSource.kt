package org.futo.circles.core.rageshake

import okhttp3.ResponseBody
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.getRageShakeUrl
import org.futo.circles.core.rageshake.io.BugreportApiService

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
            bugreportApiService.sendBugReport(getRageShakeUrl(), data)
        }
    }
}