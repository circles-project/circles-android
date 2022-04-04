package com.futo.circles.model

import com.futo.circles.core.list.IdEntity

data class GroupListItem(
    override val id: String,
    val title: String,
    val topic: String,
    val isEncrypted: Boolean,
    val avatarUrl: String,
    val membersCount: Int,
    val timestamp: Long
) : IdEntity<String>