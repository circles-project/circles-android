package org.futo.circles.settings.model

import org.futo.circles.core.model.CirclesUserSummary

data class PeopleCategoryData(
    val count: Int,
    val type: PeopleCategoryType,
    val listData: List<CirclesUserSummary>
)