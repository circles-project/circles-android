package org.futo.circles.feature.room.select_users

interface SelectUsersListener {
    fun onUserSelected(usersIds: List<String>)
}