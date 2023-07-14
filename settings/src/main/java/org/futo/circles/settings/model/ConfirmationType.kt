package org.futo.circles.settings.model

import org.futo.circles.core.model.ConfirmationType
import org.futo.circles.settings.R

data class DeactivateAccount(
    override val titleRes: Int = R.string.deactivate_my_account,
    override val messageRes: Int = R.string.deactivate_message,
    override val positiveButtonRes: Int = R.string.deactivate
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

