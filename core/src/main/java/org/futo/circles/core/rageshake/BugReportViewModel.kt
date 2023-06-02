package org.futo.circles.core.rageshake

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.ResponseBody
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.provider.MatrixSessionProvider

class BugReportViewModel(
    private val bugReportDataSource: BugReportDataSource
) : ViewModel() {

    val sendReportLiveData = SingleEventLiveData<Response<ResponseBody>>()
    val threePidLiveData =
        MatrixSessionProvider.currentSession?.profileService()?.getThreePidsLive(true)
    val screenshotLiveData = MutableLiveData(bugReportDataSource.getScreenShot())

    fun sendReport(description: String, email: String, sendLogs: Boolean, sendScreenshot: Boolean) {
        launchBg {
            val result =
                bugReportDataSource.sendReport(description, email, sendLogs, sendScreenshot)
            sendReportLiveData.postValue(result)
        }
    }
}