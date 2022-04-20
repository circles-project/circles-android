package com.futo.circles.feature.group_members.change_role

interface ChangeAccessLevelListener {
    fun onChangeAccessLevel(userId: String, levelValue: Int)
}