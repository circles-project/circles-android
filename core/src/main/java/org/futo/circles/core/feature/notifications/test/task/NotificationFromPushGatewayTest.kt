package org.futo.circles.core.feature.notifications.test.task

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.futo.circles.core.R
import org.futo.circles.core.base.ErrorParser
import org.futo.circles.core.extensions.coroutineScope
import org.futo.circles.core.feature.notifications.PushersManager
import org.futo.circles.core.model.TaskStatus
import org.futo.circles.core.provider.MatrixSessionProvider
import javax.inject.Inject

class NotificationFromPushGatewayTest @Inject constructor(
    @ApplicationContext private val context: Context,
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
                            status = TaskStatus.SUCCESS
                        } else {
                            description =
                                context.getString(R.string.settings_troubleshoot_test_push_loop_waiting_for_push)
                            status = TaskStatus.RUNNING
                        }
                        updateTestInfo()
                    },
                    {
                        description = ErrorParser.getErrorMessage(it)
                        status = TaskStatus.FAILED
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
        status = TaskStatus.SUCCESS
        updateTestInfo()
    }
}
