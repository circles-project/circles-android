package org.futo.circles.core.feature.room.manage_members.change_role

interface ChangeAccessLevelListener {
    fun onChangeAccessLevel(userId: String, levelValue: Int)
}