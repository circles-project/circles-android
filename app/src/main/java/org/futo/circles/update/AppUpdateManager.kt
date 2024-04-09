package org.futo.circles.update

import android.app.Activity

interface AppUpdateManager {
    fun launchUpdateIfAvailable(activity: Activity)

}