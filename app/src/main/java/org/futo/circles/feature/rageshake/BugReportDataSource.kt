package org.futo.circles.feature.rageshake

class BugReportDataSource(private val bugReportDataCollector: BugReportDataCollector) {

    fun getScreenShot() = bugReportDataCollector.screenshot

    fun sendReport(
        description: String,
        contactInfo: String,
        sendLogs: Boolean,
        sendScreenshot: Boolean
    ) {
        val data =
            bugReportDataCollector.collectData(description, contactInfo, sendLogs, sendScreenshot)
    }
}