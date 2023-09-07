package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.futo.circles.core.extensions.isCurrentUserAbleToBan
import org.futo.circles.core.extensions.isCurrentUserAbleToChangeLevelFor
import org.futo.circles.core.extensions.isCurrentUserAbleToKick
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.databinding.ViewManageMembersOptionsBinding
import org.futo.circles.core.room.manage_members.ManageMembersOptionsListener
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent

class ManageMembersOptionsView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewManageMembersOptionsBinding.inflate(LayoutInflater.from(context), this)

    private var listener: ManageMembersOptionsListener? = null
    private var userId: String? = null
    private var powerLevelsContent: PowerLevelsContent? = null

    init {
        with(binding) {
            btnChangeAccessLevel.setOnClickListener {
                userId?.let { id ->
                    powerLevelsContent?.let { listener?.onSetAccessLevel(id, it) }
                }
            }
            btnRemove.setOnClickListener {
                userId?.let { listener?.onRemoveUser(it) }
            }
            btnBan.setOnClickListener {
                userId?.let { listener?.onBanUser(it) }
            }
        }
    }


    fun setListener(callback: ManageMembersOptionsListener) {
        listener = callback
    }

    fun setData(userId: String, powerLevelsContent: PowerLevelsContent) {
        this.userId = userId
        this.powerLevelsContent = powerLevelsContent
        with(binding) {
            btnChangeAccessLevel.setIsVisible(
                powerLevelsContent.isCurrentUserAbleToChangeLevelFor(userId)
            )
            btnRemove.setIsVisible(powerLevelsContent.isCurrentUserAbleToKick())
            btnBan.setIsVisible(powerLevelsContent.isCurrentUserAbleToBan())
        }
    }

}