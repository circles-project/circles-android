package org.futo.circles.core.model

import org.futo.circles.core.base.list.IdEntity

data class CirclesUserSummary(
    override val id: String,
    val name: String,
    val avatarUrl: String
) : IdEntity<String>