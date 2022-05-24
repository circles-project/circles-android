package com.futo.circles.feature.settings.active_sessions.list

import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.model.*

private enum class ActiveSessionViewTypes { Header, Session }

class ActiveSessionsAdapter(
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
            ActiveSessionViewTypes.Session -> SessionItemViewHolder(parent = parent)
        }
    }

    override fun onBindViewHolder(holder: ActiveSessionsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}