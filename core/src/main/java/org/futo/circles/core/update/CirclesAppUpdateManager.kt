package org.futo.circles.core.update

import android.app.Activity

interface CirclesAppUpdateManager {
    fun launchUpdateIfAvailable(activity: Activity)

}