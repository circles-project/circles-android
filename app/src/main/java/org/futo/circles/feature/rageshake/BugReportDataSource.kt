package org.futo.circles.feature.rageshake

class BugReportDataSource(private val bugReportDataCollector: BugReportDataCollector) {

    fun getScreenShot() = bugReportDataCollector.screenshot
}