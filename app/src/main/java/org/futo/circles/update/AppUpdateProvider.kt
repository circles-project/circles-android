package org.futo.circles.update

import android.app.Activity

interface AppUpdateProvider {

    fun getManager(): AppUpdateManager?

}