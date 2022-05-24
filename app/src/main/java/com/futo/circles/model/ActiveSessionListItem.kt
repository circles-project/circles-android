package com.futo.circles.model

import com.futo.circles.core.list.IdEntity

sealed class ActiveSessionListItem : IdEntity<String>

data class SessionHeader(
    val name: String
) : ActiveSessionListItem() {
    override val id: String = name
}

data class ActiveSession(
    val name: String,

    ) : ActiveSessionListItem() {
    override val id: String = name
}