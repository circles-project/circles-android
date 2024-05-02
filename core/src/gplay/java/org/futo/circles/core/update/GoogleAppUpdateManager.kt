package org.futo.circles.core.update

import android.app.Activity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability


class GoogleAppUpdateManager : CirclesAppUpdateManager {
    override fun launchUpdateIfAvailable(activity: Activity) {
        val manager = AppUpdateManagerFactory.create(activity)
        checkForAvailableUpdate(manager) { updateInfo ->
            startUpdateFlow(activity, manager, updateInfo)
        }
    }

    private fun checkForAvailableUpdate(
        manager: AppUpdateManager,
        onAvailable: (updateInfo: AppUpdateInfo) -> Unit
    ) {
        manager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                onAvailable(appUpdateInfo)
            }
        }
    }

    private fun startUpdateFlow(
        activity: Activity,
        manager: AppUpdateManager,
        updateInfo: AppUpdateInfo
    ) {
        manager.startUpdateFlow(
            updateInfo,
            activity,
            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
        )
    }


}