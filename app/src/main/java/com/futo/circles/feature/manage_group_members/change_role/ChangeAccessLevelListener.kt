package com.futo.circles.feature.manage_group_members.change_role

interface ChangeAccessLevelListener {
    fun onChangeAccessLevel(userId: String, levelValue: Int)
}