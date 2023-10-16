package org.futo.circles.core.feature.select_users

interface SelectUsersListener {
    fun onUserSelected(usersIds: List<String>)
}