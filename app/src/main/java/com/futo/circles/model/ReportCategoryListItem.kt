package com.futo.circles.model

import com.futo.circles.core.list.IdEntity

data class ReportCategoryListItem(
    override val id: Int,
    val name: String,
    val isSelected: Boolean = false
) : IdEntity<Int>