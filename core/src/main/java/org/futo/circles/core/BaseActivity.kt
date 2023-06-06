package org.futo.circles.core

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import org.futo.circles.core.extensions.disableScreenScale
import org.futo.circles.core.rageshake.BugReportDataCollector
import org.futo.circles.core.rageshake.RageShake
import javax.inject.Inject

abstract class BaseActivity(contentLayoutId: Int) : AppCompatActivity(contentLayoutId) {

    @Inject
    lateinit var bugReportDataCollector: BugReportDataCollector

    private val rageShake by lazy { RageShake(this, bugReportDataCollector) }
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextWrapper(newBase.disableScreenScale()))
    }

    override fun onResume() {
        super.onResume()
        if (CirclesAppConfig.isRageshakeEnabled) rageShake.start()
    }

    override fun onPause() {
        super.onPause()
        rageShake.stop()
    }
}