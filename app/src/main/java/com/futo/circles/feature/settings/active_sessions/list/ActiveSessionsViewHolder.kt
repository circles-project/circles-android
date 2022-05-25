package com.futo.circles.feature.settings.active_sessions.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.R
import com.futo.circles.core.list.ViewBindingHolder
import com.futo.circles.databinding.ActiveSessionListItemBinding
import com.futo.circles.databinding.InviteHeaderListItemBinding
import com.futo.circles.model.ActiveSession
import com.futo.circles.model.ActiveSessionListItem
import com.futo.circles.model.SessionHeader

abstract class ActiveSessionsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: ActiveSessionListItem)
}

class SessionItemViewHolder(
    parent: ViewGroup,
    private val activeSessionClickListener: ActiveSessionClickListener
) : ActiveSessionsViewHolder(inflate(parent, ActiveSessionListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ActiveSessionListItemBinding

    override fun bind(data: ActiveSessionListItem) {
        if (data !is ActiveSession) return

        with(binding) {
            lRoot.setOnClickListener { activeSessionClickListener.onItemClicked(data.id) }

            tvDeviceName.text = data.deviceInfo.displayName ?: data.id
            tvDeviceId.text = data.cryptoDeviceInfo.deviceId

            ivVerified.setImageResource(
                if (data.cryptoDeviceInfo.isVerified) R.drawable.ic_verified
                else R.drawable.ic_unverified
            )

            ivOptionsArrow.setImageResource(
                if (data.isOptionsVisible) R.drawable.ic_keyboard_arrow_up
                else R.drawable.ic_keyboard_arrow_down
            )
        }
    }
}

class SessionHeaderViewHolder(
    parent: ViewGroup,
) : ActiveSessionsViewHolder(inflate(parent, InviteHeaderListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as InviteHeaderListItemBinding

    override fun bind(data: ActiveSessionListItem) {
        if (data !is SessionHeader) return
        binding.tvHeader.text = data.name
    }
}
