package org.futo.circles.feature.notifications

interface AutoAcceptInvites {

    val isEnabled: Boolean

    val hideInvites: Boolean
        get() = isEnabled
}

fun AutoAcceptInvites.showInvites() = !hideInvites

class CompileTimeAutoAcceptInvites : AutoAcceptInvites {
    override val isEnabled = false
}
