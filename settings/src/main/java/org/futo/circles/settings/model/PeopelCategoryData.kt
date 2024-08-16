package org.futo.circles.settings.model

data class PeopleCategoryData(
    val count: Int,
    val type: PeopleCategoryType,
    val listData: List<PeopleListItem>
)