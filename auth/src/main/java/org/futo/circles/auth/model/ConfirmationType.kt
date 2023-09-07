package org.futo.circles.auth.model

import org.futo.circles.auth.R
import org.futo.circles.core.model.ConfirmationType

data class RemoveUser(
    override val titleRes: Int = org.futo.circles.core.R.string.remove_user,
    override val messageRes: Int = org.futo.circles.core.R.string.remove_user_message,
    override val positiveButtonRes: Int = org.futo.circles.core.R.string.remove
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)

data class SwitchUser(
    override val titleRes: Int = R.string.switch_user,
    override val messageRes: Int = R.string.switch_user_message,
    override val positiveButtonRes: Int = R.string.switch_str
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)

data class LogOut(
    override val titleRes: Int = R.string.log_out,
    override val messageRes: Int = R.string.log_out_message,
    override val positiveButtonRes: Int = R.string.log_out
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)

data class RemoveSession(
    override val titleRes: Int = R.string.remove_session,
    override val messageRes: Int = R.string.remove_session_message,
    override val positiveButtonRes: Int =  org.futo.circles.core.R.string.remove
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)

data class ResetKeys(
    override val titleRes: Int = R.string.reset_keys,
    override val messageRes: Int = R.string.reset_keys_message,
    override val positiveButtonRes: Int = R.string.confirm
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)