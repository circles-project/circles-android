package org.futo.circles.model

import org.futo.circles.core.list.IdEntity

data class TermsListItem(
    override val id: Int,
    val name: String,
    val url: String,
    val isChecked: Boolean = false
) : IdEntity<Int>