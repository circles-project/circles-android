package org.futo.circles.core.model

import org.futo.circles.core.base.list.IdEntity

data class ReactionsData(
    val key: String,
    val count: Int,
    val addedByMe: Boolean
) : IdEntity<String> {
    override val id: String = key
}