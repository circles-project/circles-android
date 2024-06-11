package org.futo.circles.core.base

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import org.matrix.android.sdk.api.extensions.tryOrNull


abstract class BaseActivity : AppCompatActivity() {

    private val noInternetConnectionPresenter = NoInternetConnectionViewPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noInternetConnectionPresenter.register(this, findRootLayout(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        noInternetConnectionPresenter.unregister()
    }

    private fun findRootLayout(activity: Activity): ViewGroup? {
        val contentView = activity.findViewById<ViewGroup>(android.R.id.content)
        return tryOrNull { contentView.getChildAt(0) as ViewGroup }
    }
}