package org.futo.circles.core.model

import org.matrix.android.sdk.api.session.pushrules.Action

data class NotificationAction(
    val shouldNotify: Boolean,
    val highlight: Boolean,
    val soundName: String?
)

fun List<Action>.toNotificationAction(): NotificationAction {
    var shouldNotify = false
    var highlight = false
    var sound: String? = null
    forEach { action ->
        when (action) {
            is Action.Notify -> shouldNotify = true
            is Action.Highlight -> highlight = action.highlight
            is Action.Sound -> sound = action.sound
        }
    }
    return NotificationAction(shouldNotify, highlight, sound)
}
