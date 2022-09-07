package org.futo.circles.core

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import org.futo.circles.extensions.disableScreenScale

abstract class BaseActivity(contentLayoutId: Int) : AppCompatActivity(contentLayoutId) {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextWrapper(newBase.disableScreenScale()))
    }
}