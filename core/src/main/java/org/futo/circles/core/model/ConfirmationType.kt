package org.futo.circles.core.model

import org.futo.circles.core.R


abstract class ConfirmationType(
    open val titleRes: Int,
    open val messageRes: Int,
    open val positiveButtonRes: Int
)

data class DeactivateAccount(
    override val titleRes: Int = org.futo.circles.core.R.string.deactivate_my_account,
    override val messageRes: Int = R.string.deactivate_message,
    override val positiveButtonRes: Int = R.string.deactivate
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)