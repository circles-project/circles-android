package org.futo.circles

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import org.futo.circles.core.BaseActivity
import org.futo.circles.provider.MatrixSessionListenerProvider
import org.futo.circles.provider.MatrixSessionProvider


class MainActivity : BaseActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setInvalidTokenListener()
        syncSessionIfCashWasCleared()
    }

    fun clearSessionAndRestart() {
        MatrixSessionProvider.clearSession()
        this.startActivity(createRestartIntent())
    }

    fun restartForClearCache() {
        this.startActivity(createRestartIntent().apply { putExtra(IS_CLEAR_CACHE, true) })
    }

    private fun syncSessionIfCashWasCleared() {
        val isClearCashReload = intent?.getBooleanExtra(IS_CLEAR_CACHE, false) ?: false
        if (isClearCashReload) MatrixSessionProvider.currentSession?.syncService()?.startSync(true)
        intent?.removeExtra(IS_CLEAR_CACHE)
    }

    private fun createRestartIntent(): Intent {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        return intent
    }

    private fun setInvalidTokenListener() {
        MatrixSessionListenerProvider.setOnInvalidTokenListener {
            Toast.makeText(this, getString(R.string.you_are_signed_out), Toast.LENGTH_LONG).show()
            clearSessionAndRestart()
        }
    }

    companion object {
        private const val IS_CLEAR_CACHE = "is_clear_cache"
        const val ROOM_ID_PARAM = "roomId"
        fun getOpenRoomIntent(context: Context, roomId: String): Intent =
            Intent(context, MainActivity::class.java).apply {
                action = "OPEN_ROOM"
                putExtra(ROOM_ID_PARAM, roomId)
            }
    }
}