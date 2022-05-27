package com.futo.circles.feature.settings.active_sessions.list

import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.model.ActiveSession
import com.futo.circles.model.ActiveSessionListItem
import com.futo.circles.model.SessionHeader

interface ActiveSessionClickListener {
    fun onItemClicked(deviceId: String)
    fun onVerifySessionClicked(deviceId: String)
    fun onRemoveSessionClicked(deviceId: String)
}

private enum class ActiveSessionViewTypes { Header, Session }

class ActiveSessionsAdapter(
    private val activeSessionClickListener: ActiveSessionClickListener
) : BaseRvAdapter<ActiveSessionListItem, ActiveSessionsViewHolder>(
    DefaultIdEntityCallback()
) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is SessionHeader -> ActiveSessionViewTypes.Header.ordinal
        is ActiveSession -> ActiveSessionViewTypes.Session.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActiveSessionsViewHolder {
        return when (ActiveSessionViewTypes.values()[viewType]) {
            ActiveSessionViewTypes.Header -> SessionHeaderViewHolder(parent)
            ActiveSessionViewTypes.Session -> SessionItemViewHolder(
                parent = parent,
                activeSessionClickListener
            )
        }
    }

    override fun onBindViewHolder(holder: ActiveSessionsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}