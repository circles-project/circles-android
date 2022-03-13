package com.futo.circles.model

import com.futo.circles.core.IdEntity

data class TermsListItem(
    override val id: Int,
    val name: String,
    val url: String,
    val isChecked: Boolean = false
) : IdEntity<Int>