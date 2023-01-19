package org.futo.circles.feature.notifications

import android.app.PendingIntent
import android.os.Build

object PendingIntentCompat {
    val FLAG_IMMUTABLE = PendingIntent.FLAG_IMMUTABLE

    val FLAG_MUTABLE = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PendingIntent.FLAG_MUTABLE
    } else {
        0
    }
}
