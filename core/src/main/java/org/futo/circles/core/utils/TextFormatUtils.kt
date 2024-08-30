package org.futo.circles.core.utils

import android.content.Context
import org.futo.circles.core.R

object TextFormatUtils {

    fun getFormattedInvitesKnocksMessage(
        context: Context,
        invitesCount: Int,
        knockRequestsCount: Int
    ): String {
        var message = context.getString(R.string.you_have)

        if (invitesCount > 0) {
            message += " " + context.resources.getQuantityString(
                R.plurals.invitations_count_format,
                invitesCount, invitesCount
            )
        }

        if (invitesCount > 0 && knockRequestsCount > 0) {
            message += " " + context.getString(R.string.and)
        }

        if (knockRequestsCount > 0) {
            message += " " + context.resources.getQuantityString(
                R.plurals.requests_count_format,
                knockRequestsCount, knockRequestsCount
            )
        }
        return message
    }
}