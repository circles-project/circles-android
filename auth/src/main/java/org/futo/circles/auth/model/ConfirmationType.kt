package org.futo.circles.auth.model

import org.futo.circles.auth.R
import org.futo.circles.core.model.ConfirmationType

data class RemoveUser(
    override val titleRes: Int = org.futo.circles.core.R.string.remove_user,
    override val messageRes: Int = org.futo.circles.core.R.string.remove_user_message,
    override val positiveButtonRes: Int = org.futo.circles.core.R.string.remove
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)

data class ForgotPassword(
    override val titleRes: Int = R.string.forgot_password,
    override val messageRes: Int = R.string.forgot_password_message,
    override val positiveButtonRes: Int = R.string.confirm
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)