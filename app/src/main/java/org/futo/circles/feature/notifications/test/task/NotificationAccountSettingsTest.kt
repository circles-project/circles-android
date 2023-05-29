package org.futo.circles.feature.notifications.test.task

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.futo.circles.R
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.extensions.coroutineScope
import org.futo.circles.model.NotificationTestStatus
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.pushrules.RuleIds
import org.matrix.android.sdk.api.session.pushrules.RuleKind

class NotificationAccountSettingsTest(private val context: Context) : BaseNotificationTest(
    R.string.settings_troubleshoot_test_account_settings_title
) {

    override fun perform() {
        val session = MatrixSessionProvider.currentSession ?: return
        val defaultRule = session.pushRuleService().getPushRules().getAllRules()
            .find { it.ruleId == RuleIds.RULE_ID_DISABLE_ALL }

        if (defaultRule != null) {
            if (!defaultRule.enabled) {
                description =
                    context.getString(R.string.settings_troubleshoot_test_account_settings_success)
                quickFix = null
                status = NotificationTestStatus.SUCCESS
            } else {
                description =
                    context.getString(R.string.settings_troubleshoot_test_account_settings_failed)
                quickFix = object : NotificationQuickFix() {
                    override fun runFix() {
                        session.coroutineScope.launch {
                            tryOrNull {
                                session.pushRuleService().updatePushRuleEnableStatus(
                                    RuleKind.OVERRIDE,
                                    defaultRule,
                                    !defaultRule.enabled
                                )
                            }
                            withContext(Dispatchers.Main) { perform() }
                        }
                    }
                }
                status = NotificationTestStatus.SUCCESS
            }
        } else {
            status = NotificationTestStatus.FAILED
        }
    }
}
