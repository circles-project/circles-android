package org.futo.circles.core.select_users

interface SelectUsersListener {
    fun onUserSelected(usersIds: List<String>)
}