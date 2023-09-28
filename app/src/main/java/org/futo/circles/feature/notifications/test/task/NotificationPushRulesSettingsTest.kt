package org.futo.circles.feature.notifications.test.task

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.R
import org.futo.circles.core.model.TaskStatus
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.model.toNotificationAction
import org.matrix.android.sdk.api.session.pushrules.RuleIds
import org.matrix.android.sdk.api.session.pushrules.getActions
import javax.inject.Inject


class NotificationPushRulesSettingsTest @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseNotificationTest(R.string.settings_troubleshoot_test_bing_settings_title) {

    private val testedRules =
        listOf(
            RuleIds.RULE_ID_CONTAIN_DISPLAY_NAME,
            RuleIds.RULE_ID_CONTAIN_USER_NAME,
            RuleIds.RULE_ID_ONE_TO_ONE_ROOM,
            RuleIds.RULE_ID_ALL_OTHER_MESSAGES_ROOMS
        )

    override fun perform() {
        val session = MatrixSessionProvider.currentSession ?: return
        val pushRules = session.pushRuleService().getPushRules().getAllRules()
        var oneOrMoreRuleIsOff = false
        var oneOrMoreRuleAreSilent = false
        testedRules.forEach { ruleId ->
            pushRules.find { it.ruleId == ruleId }?.let { rule ->
                val actions = rule.getActions()
                val notifAction = actions.toNotificationAction()
                if (!rule.enabled || !notifAction.shouldNotify) {
                    oneOrMoreRuleIsOff = true
                } else if (notifAction.soundName == null) {
                    oneOrMoreRuleAreSilent = true
                }
            }
        }

        if (oneOrMoreRuleIsOff) {
            description =
                context.getString(R.string.settings_troubleshoot_test_bing_settings_failed)
            status = TaskStatus.FAILED
        } else {
            description = if (oneOrMoreRuleAreSilent) {
                context.getString(R.string.settings_troubleshoot_test_bing_settings_success_with_warn)
            } else ""
            status = TaskStatus.SUCCESS
        }
    }
}
