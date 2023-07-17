package org.futo.circles.auth.feature.active_sessions.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.ListItemActiveSessionBinding
import org.futo.circles.auth.model.ActiveSession
import org.futo.circles.auth.model.ActiveSessionListItem
import org.futo.circles.auth.model.SessionHeader
import org.futo.circles.core.databinding.ListItemInviteHeaderBinding
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.list.ViewBindingHolder

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
                if (data.isCrossSigningVerified()) R.drawable.ic_verified
                else R.drawable.ic_unverified
            )

            ivOptionsArrow.setImageResource(
                if (data.isOptionsVisible) org.futo.circles.core.R.drawable.ic_keyboard_arrow_up
                else org.futo.circles.core.R.drawable.ic_keyboard_arrow_down
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
