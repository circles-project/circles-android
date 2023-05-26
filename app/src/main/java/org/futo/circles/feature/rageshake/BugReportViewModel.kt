package org.futo.circles.feature.rageshake

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.ResponseBody
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.feature.sign_up.setup_profile.SetupProfileDataSource

class BugReportViewModel(
    profileDataSource: SetupProfileDataSource,
    private val bugReportDataSource: BugReportDataSource
) : ViewModel() {

    val sendReportLiveData = SingleEventLiveData<Response<ResponseBody>>()
    val threePidLiveData = profileDataSource.threePidLiveData
    val screenshotLiveData = MutableLiveData(bugReportDataSource.getScreenShot())

    fun sendReport(description: String, email: String, sendLogs: Boolean, sendScreenshot: Boolean) {
        launchBg {
            val result =
                bugReportDataSource.sendReport(description, email, sendLogs, sendScreenshot)
            sendReportLiveData.postValue(result)
        }
    }
}