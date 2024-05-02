package org.futo.circles

import android.content.Context
import android.content.Intent
import android.os.Bundle
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.base.BaseActivity
import org.futo.circles.core.base.DeepLinkIntentHandler
import org.futo.circles.settings.SessionHolderActivity
import org.futo.circles.core.feature.markdown.MarkdownParser
import org.futo.circles.core.feature.whats_new.WhatsNewDialog
import org.futo.circles.core.update.AppUpdateProvider
import org.futo.circles.core.utils.LauncherActivityUtils
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : BaseActivity(R.layout.activity_main), SessionHolderActivity {

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

    override fun clearSessionAndRestart() {
        LauncherActivityUtils.clearSessionAndRestart(this, getSelfIntent(this))
    }

    override fun stopSyncAndRestart() {
        LauncherActivityUtils.stopSyncAndRestart(this, getSelfIntent(this))
    }

    private fun getSelfIntent(context: Context) = Intent(context, MainActivity::class.java)
}