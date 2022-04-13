package com.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.futo.circles.databinding.ManageMembersOptionsViewBinding

interface ManageMembersOptionsListener {

    fun onSetAccessLevel(userId: String)

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

    init {
        with(binding) {
            btnChangeAccessLevel.setOnClickListener {
                userId?.let { listener?.onSetAccessLevel(it) }
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

    fun setData(userId: String) {
        this.userId = userId
    }

}