package org.futo.circles.feature.notifications.test.task

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.futo.circles.R
import org.futo.circles.core.ErrorParser
import org.futo.circles.extensions.coroutineScope
import org.futo.circles.feature.notifications.PushersManager
import org.futo.circles.model.NotificationTestStatus
import org.futo.circles.provider.MatrixSessionProvider

class NotificationFromPushGatewayTest(
    private val context: Context,
    private val pushersManager: PushersManager
) : BaseNotificationTest(R.string.settings_troubleshoot_test_push_loop_title) {

    private var action: Job? = null
    private var pushReceived: Boolean = false

    override fun perform() {
        pushReceived = false
        action = MatrixSessionProvider.currentSession?.coroutineScope?.launch {
            val result = runCatching { pushersManager.testPush() }

            withContext(Dispatchers.Main) {
                status = result
                    .fold(
                        {
                            if (pushReceived) {
                                // Push already received (race condition)
                                description =
                                    context.getString(R.string.settings_troubleshoot_test_push_loop_success)
                                NotificationTestStatus.SUCCESS
                            } else {
                                // Wait for the push to be received
                                description =
                                    context.getString(R.string.settings_troubleshoot_test_push_loop_waiting_for_push)
                                NotificationTestStatus.RUNNING
                            }
                        },
                        {
                            description = ErrorParser.getErrorMessage(it)
                            NotificationTestStatus.FAILED
                        }
                    )
            }
        }
    }

    fun onPushReceived() {
        pushReceived = true
        description =
            context.getString(R.string.settings_troubleshoot_test_push_loop_success)
        status = NotificationTestStatus.SUCCESS
    }

    fun cancel() {
        action?.cancel()
    }
}
