package org.futo.circles.model

import org.futo.circles.core.base.list.IdEntity

data class ReportCategoryListItem(
    override val id: Int,
    val name: String,
    val isSelected: Boolean = false
) : IdEntity<Int>