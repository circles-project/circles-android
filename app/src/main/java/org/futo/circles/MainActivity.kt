package org.futo.circles

import android.content.Context
import android.content.Intent
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.base.BaseActivity
import org.futo.circles.core.feature.markdown.MarkdownParser
import org.futo.circles.core.utils.LauncherActivityUtils
import org.futo.circles.core.base.DeepLinkIntentHandler
import org.futo.circles.core.feature.whats_new.WhatsNewDialog
import org.futo.circles.update.AppUpdateProvider
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : BaseActivity(R.layout.activity_main) {

    @Inject
    lateinit var appUpdateProvider: AppUpdateProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LauncherActivityUtils.setInvalidTokenListener(this, getSelfIntent(this))
        MarkdownParser.initBuilder(this)
        LauncherActivityUtils.syncSessionIfCashWasCleared(this)
        WhatsNewDialog.showIfNeed(this)
        appUpdateProvider.getManager()?.launchUpdateIfAvailable(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleOpenFromIntent()
    }

    private fun handleOpenFromIntent() {
        supportFragmentManager.fragments.firstOrNull()?.childFragmentManager?.fragments?.firstOrNull { it is DeepLinkIntentHandler }
            ?.let { (it as DeepLinkIntentHandler).onNewIntent() }
    }

    fun clearSessionAndRestart() {
        LauncherActivityUtils.clearSessionAndRestart(this, getSelfIntent(this))
    }

    fun stopSyncAndRestart() {
        LauncherActivityUtils.stopSyncAndRestart(this, getSelfIntent(this))
    }


    companion object {
        const val ROOM_ID_PARAM = "roomId"
        fun getSelfIntent(context: Context) = Intent(context, MainActivity::class.java)
        fun getOpenRoomIntent(context: Context, roomId: String): Intent =
            getSelfIntent(context).apply {
                action = "OPEN_ROOM"
                putExtra(ROOM_ID_PARAM, roomId)
            }
    }
}