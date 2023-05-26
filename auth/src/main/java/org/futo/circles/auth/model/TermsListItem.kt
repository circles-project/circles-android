package org.futo.circles.auth.model

import org.futo.circles.core.list.IdEntity

data class TermsListItem(
    override val id: Int,
    val name: String,
    val url: String,
    val isChecked: Boolean = false
) : org.futo.circles.core.list.IdEntity<Int>