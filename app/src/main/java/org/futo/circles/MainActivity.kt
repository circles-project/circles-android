package org.futo.circles

import android.content.Context
import android.content.ContextWrapper
import androidx.appcompat.app.AppCompatActivity
import org.futo.circles.extensions.disableScreenScale


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ContextWrapper(newBase.disableScreenScale()))
    }
}