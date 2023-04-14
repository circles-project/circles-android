package org.futo.circles.feature.notifications

import android.content.Context
import org.unifiedpush.android.connector.MessagingReceiver


class UnifiedPushMessagingReceiver : MessagingReceiver() {

    override fun onMessage(context: Context, message: ByteArray, instance: String) {
    }

    override fun onNewEndpoint(context: Context, endpoint: String, instance: String) {

    }

    override fun onRegistrationFailed(context: Context, instance: String) {

    }

    override fun onUnregistered(context: Context, instance: String) {

    }
}