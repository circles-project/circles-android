package org.futo.circles.auth.model

import org.futo.circles.auth.R
import org.futo.circles.core.model.ConfirmationType

data class RemoveUser(
    override val titleRes: Int = R.string.remove_user,
    override val messageRes: Int = R.string.remove_user_message,
    override val positiveButtonRes: Int = R.string.remove
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)