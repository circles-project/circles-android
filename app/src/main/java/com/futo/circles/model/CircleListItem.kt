package com.futo.circles.model

import com.futo.circles.core.list.IdEntity

data class CircleListItem(
    override val id: String,
    val name: String,
    val followingCount: Int,
    val followedByCount: Int,
    val avatarUrl: String
) : IdEntity<String>