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

data class LeaveGroup(
    override val titleRes: Int = R.string.leave_group,
    override val messageRes: Int = R.string.leave_group_message,
    override val positiveButtonRes: Int = R.string.leave
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)

data class DeleteGroup(
    override val titleRes: Int = R.string.delete_group,
    override val messageRes: Int = R.string.delete_group_message,
    override val positiveButtonRes: Int = R.string.delete
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)

data class DeleteCircle(
    override val titleRes: Int = R.string.delete_circle,
    override val messageRes: Int = R.string.delete_circle_message,
    override val positiveButtonRes: Int = R.string.delete
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)

data class RemoveRoomUser(
    override val titleRes: Int = R.string.remove_user,
    override val messageRes: Int = R.string.remove_user_in_room_message,
    override val positiveButtonRes: Int = org.futo.circles.core.R.string.remove
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)

data class BanUser(
    override val titleRes: Int = R.string.ban_user,
    override val messageRes: Int = R.string.ban_user_message,
    override val positiveButtonRes: Int = R.string.ban
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)

data class UnbanUser(
    override val titleRes: Int = R.string.unban_user,
    override val messageRes: Int = R.string.unban_user_message,
    override val positiveButtonRes: Int = R.string.unban
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)

data class CancelInvite(
    override val titleRes: Int = R.string.cancel_invite,
    override val messageRes: Int = R.string.cancel_invite_message,
    override val positiveButtonRes: Int = android.R.string.ok
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)

data class ResendInvite(
    override val titleRes: Int = R.string.resend_invite,
    override val messageRes: Int = R.string.resend_invite_message,
    override val positiveButtonRes: Int = android.R.string.ok
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)

data class DeactivateAccount(
    override val titleRes: Int = R.string.deactivate_my_account,
    override val messageRes: Int = R.string.deactivate_message,
    override val positiveButtonRes: Int = R.string.deactivate
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)