package org.futo.circles.feature.settings.active_sessions.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.R
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.databinding.ListItemActiveSessionBinding
import org.futo.circles.databinding.ListItemInviteHeaderBinding
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.model.ActiveSession
import org.futo.circles.model.ActiveSessionListItem
import org.futo.circles.model.SessionHeader

abstract class ActiveSessionsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: ActiveSessionListItem)
}

class SessionItemViewHolder(
    parent: ViewGroup,
    private val activeSessionClickListener: ActiveSessionClickListener
) : ActiveSessionsViewHolder(inflate(parent, ListItemActiveSessionBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemActiveSessionBinding

    override fun bind(data: ActiveSessionListItem) {
        if (data !is ActiveSession) return

        with(binding) {
            lRoot.setOnClickListener { activeSessionClickListener.onItemClicked(data.id) }

            tvDeviceName.text = data.deviceInfo.displayName ?: data.id
            tvDeviceId.text = data.cryptoDeviceInfo.deviceId
            vInfo.setData(data, activeSessionClickListener)
            vInfo.setIsVisible(data.isOptionsVisible)

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
) : ActiveSessionsViewHolder(inflate(parent, ListItemInviteHeaderBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInviteHeaderBinding

    override fun bind(data: ActiveSessionListItem) {
        if (data !is SessionHeader) return
        binding.tvHeader.text = data.name
    }
}
