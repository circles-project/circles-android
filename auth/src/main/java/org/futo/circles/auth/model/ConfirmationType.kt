package org.futo.circles.auth.model

import org.futo.circles.auth.R
import org.futo.circles.core.model.ConfirmationType

data class RemoveUser(
    override val titleRes: Int = R.string.remove_user,
    override val messageRes: Int = R.string.remove_user_message,
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