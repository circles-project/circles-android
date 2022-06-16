package org.futo.circles.feature.room.manage_members.change_role

interface ChangeAccessLevelListener {
    fun onChangeAccessLevel(userId: String, levelValue: Int)
}