package org.futo.circles.model

import org.futo.circles.R
import org.futo.circles.core.model.ConfirmationType


data class RemovePost(
    override val titleRes: Int = R.string.remove_post,
    override val messageRes: Int = R.string.remove_post_message,
    override val positiveButtonRes: Int = org.futo.circles.core.R.string.remove
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)

data class IgnoreSender(
    override val titleRes: Int = R.string.ignore_sender,
    override val messageRes: Int = org.futo.circles.core.R.string.ignore_user_message,
    override val positiveButtonRes: Int = org.futo.circles.core.R.string.ignore
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)

data class EndPoll(
    override val titleRes: Int = R.string.end_poll,
    override val messageRes: Int = R.string.end_poll_message,
    override val positiveButtonRes: Int = R.string.end_poll
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)



