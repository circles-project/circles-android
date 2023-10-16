package org.futo.circles.model

import org.futo.circles.core.base.list.IdEntity

data class StyleBarListItem(
    override val id: Int,
    val iconResId: Int,
    val isSelected: Boolean = false
) : IdEntity<Int>