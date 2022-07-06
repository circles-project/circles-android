package org.futo.circles.model

import org.futo.circles.core.list.IdEntity

data class SystemNoticeListItem(
    override val id: String,
    val message: String,
    val time: Long
) : IdEntity<String>