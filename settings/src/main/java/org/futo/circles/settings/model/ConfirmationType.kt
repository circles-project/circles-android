package org.futo.circles.settings.model

import org.futo.circles.core.model.ConfirmationType
import org.futo.circles.settings.R

data class DeactivateAccount(
    override val titleRes: Int = R.string.deactivate_my_account,
    override val messageRes: Int = R.string.deactivate_message,
    override val positiveButtonRes: Int = R.string.deactivate
) : ConfirmationType(titleRes, messageRes, positiveButtonRes)



