package com.futo.circles.feature.people.user

import android.content.Context
import com.futo.circles.R
import com.futo.circles.provider.MatrixSessionProvider

class UserDataSource(
    context: Context,
    userId: String
) {

    private val session by lazy {
        MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
            context.getString(R.string.session_is_not_created)
        )
    }

    val userLiveData = session.userService().getUserLive(userId)
}