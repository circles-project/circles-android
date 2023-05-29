package org.futo.circles.feature.notifications.test.task

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.futo.circles.R
import org.futo.circles.core.ErrorParser
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.extensions.coroutineScope
import org.futo.circles.feature.notifications.PushersManager
import org.futo.circles.model.NotificationTestStatus

class NotificationFromPushGatewayTest(
    private val context: Context,
    private val pushersManager: PushersManager
) : BaseNotificationTest(R.string.settings_troubleshoot_test_push_loop_title),
    TestPushDisplayEvenReceiver {

    private var pushReceived: Boolean = false

    override fun perform() {
        pushReceived = false
        MatrixSessionProvider.currentSession?.coroutineScope?.launch {
            val result = runCatching { pushersManager.testPush() }

            withContext(Dispatchers.Main) {
                result.fold(
                    {
                        if (pushReceived) {
                            description =
                                context.getString(R.string.settings_troubleshoot_test_push_loop_success)
                            status = NotificationTestStatus.SUCCESS
                        } else {
                            description =
                                context.getString(R.string.settings_troubleshoot_test_push_loop_waiting_for_push)
                            status = NotificationTestStatus.RUNNING
                        }
                        updateTestInfo()
                    },
                    {
                        description = ErrorParser.getErrorMessage(it)
                        status = NotificationTestStatus.FAILED
                        updateTestInfo()
                    }
                )
            }
        }
    }

    override fun onTestPushDisplayed() {
        pushReceived = true
        description =
            context.getString(R.string.settings_troubleshoot_test_push_loop_success)
        status = NotificationTestStatus.SUCCESS
        updateTestInfo()
    }
}
