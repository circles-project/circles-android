package org.futo.circles.settings

interface SessionHolderActivity {
    fun clearSessionAndRestart()

    fun stopSyncAndRestart()
}