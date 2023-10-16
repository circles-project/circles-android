package org.futo.circles.auth.model

import org.futo.circles.core.base.list.IdEntity

data class SubscriptionListItem(
    override val id: String,
    val name: String,
    val description: String,
    val price: String,
    val duration: String
) : IdEntity<String>