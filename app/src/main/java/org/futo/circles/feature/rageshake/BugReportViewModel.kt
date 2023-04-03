package org.futo.circles.feature.rageshake

import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.feature.sign_up.setup_profile.SetupProfileDataSource

class BugReportViewModel(
    profileDataSource: SetupProfileDataSource
) : ViewModel() {

    val sendReportLiveData = SingleEventLiveData<Response<Unit>>()
    val threePidLiveData = profileDataSource.threePidLiveData

    fun sendReport(description: String, email: String, sendLogs: Boolean, sendScreenshot: Boolean) {

    }
}