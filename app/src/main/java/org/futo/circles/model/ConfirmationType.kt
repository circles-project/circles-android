package org.futo.circles.model

import org.futo.circles.R
import org.futo.circles.core.model.ConfirmationType


data class IgnoreUser(
    override val titleRes: Int = R.string.ignore,
    override val messageRes: Int = R.string.ignore_user_message,
    override val positiveButtonRes: Int = R.string.ignore
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)

data class UnfollowUser(
    override val titleRes: Int = R.string.unfollow,
    override val messageRes: Int = R.string.unfollow_user_message,
    override val positiveButtonRes: Int = R.string.unfollow
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)

data class UnfollowTimeline(
    override val titleRes: Int = R.string.unfollow,
    override val messageRes: Int = R.string.unfollow_user_timeline,
    override val positiveButtonRes: Int = R.string.unfollow
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)


data class RemovePost(
    override val titleRes: Int = R.string.remove_post,
    override val messageRes: Int = R.string.remove_post_message,
    override val positiveButtonRes: Int = org.futo.circles.core.R.string.remove
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)

data class IgnoreSender(
    override val titleRes: Int = R.string.ignore_sender,
    override val messageRes: Int = R.string.ignore_user_message,
    override val positiveButtonRes: Int = R.string.ignore
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)

data class EndPoll(
    override val titleRes: Int = R.string.end_poll,
    override val messageRes: Int = R.string.end_poll_message,
    override val positiveButtonRes: Int = R.string.end_poll
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)



