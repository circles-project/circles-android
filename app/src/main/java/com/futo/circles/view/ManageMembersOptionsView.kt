package com.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.futo.circles.databinding.ManageMembersOptionsViewBinding
import com.futo.circles.extensions.isCurrentUserAbleToBan
import com.futo.circles.extensions.isCurrentUserAbleToChangeSettings
import com.futo.circles.extensions.isCurrentUserAbleToKick
import com.futo.circles.extensions.setIsVisible
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent

interface ManageMembersOptionsListener {

    fun onSetAccessLevel(userId: String, levelValue: Int)

    fun onRemoveUser(userId: String)

    fun onBanUser(userId: String)

}


class ManageMembersOptionsView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        ManageMembersOptionsViewBinding.inflate(LayoutInflater.from(context), this)

    private var listener: ManageMembersOptionsListener? = null
    private var userId: String? = null
    private var roleValue: Int? = null

    init {
        with(binding) {
            btnChangeAccessLevel.setOnClickListener {
                userId?.let { id ->
                    roleValue?.let { listener?.onSetAccessLevel(id, it) }
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

    fun setData(userId: String, roleValue: Int, powerLevelsContent: PowerLevelsContent) {
        this.userId = userId
        this.roleValue = roleValue
        with(binding) {
            btnChangeAccessLevel.setIsVisible(powerLevelsContent.isCurrentUserAbleToChangeSettings())
            btnRemove.setIsVisible(powerLevelsContent.isCurrentUserAbleToKick())
            btnBan.setIsVisible(powerLevelsContent.isCurrentUserAbleToBan())
        }
    }

}