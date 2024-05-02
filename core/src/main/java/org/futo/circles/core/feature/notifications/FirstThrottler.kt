package org.futo.circles.core.feature.notifications

import android.os.SystemClock


class FirstThrottler(private val minimumInterval: Long = 800) {
    private var lastDate = 0L

    sealed class CanHandlerResult {
        object Yes : CanHandlerResult()
        data class No(val shouldWaitMillis: Long) : CanHandlerResult()

        fun waitMillis(): Long {
            return when (this) {
                Yes -> 0
                is No -> shouldWaitMillis
            }
        }
    }

    fun canHandle(): CanHandlerResult {
        val now = SystemClock.elapsedRealtime()
        val delaySinceLast = now - lastDate
        if (delaySinceLast > minimumInterval) {
            lastDate = now
            return CanHandlerResult.Yes
        }
        return CanHandlerResult.No(minimumInterval - delaySinceLast)
    }
}
