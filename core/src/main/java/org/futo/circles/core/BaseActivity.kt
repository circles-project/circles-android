package org.futo.circles.core

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.internal.findRootView
import org.futo.circles.core.rageshake.BugReportDataCollector
import org.futo.circles.core.rageshake.RageShake
import javax.inject.Inject


abstract class BaseActivity(contentLayoutId: Int) : AppCompatActivity(contentLayoutId) {

    @Inject
    lateinit var bugReportDataCollector: BugReportDataCollector

    private val rageShake by lazy { RageShake(this, bugReportDataCollector) }
    private val noInternetConnectionPresenter = NoInternetConnectionViewPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noInternetConnectionPresenter.register(this, findRootView(this) as? ViewGroup)
    }

    override fun onResume() {
        super.onResume()
        if (CirclesAppConfig.isRageshakeEnabled) rageShake.start()
    }

    override fun onPause() {
        super.onPause()
        if (CirclesAppConfig.isRageshakeEnabled) rageShake.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        noInternetConnectionPresenter.unregister()
    }
}