package org.futo.circles.base

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

interface ExpandableItemsDataSource {

    val itemsWithVisibleOptionsFlow: MutableStateFlow<MutableSet<String>>

    fun toggleOptionsVisibilityFor(id: String) {
        val isOptionsVisible = itemsWithVisibleOptionsFlow.value.contains(id)
        itemsWithVisibleOptionsFlow.update { value ->
            val newSet = mutableSetOf<String>().apply { addAll(value) }
            if (isOptionsVisible) newSet.remove(id)
            else newSet.add(id)
            newSet
        }
    }
}