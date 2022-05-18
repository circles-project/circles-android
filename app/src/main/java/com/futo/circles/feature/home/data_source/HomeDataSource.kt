package com.futo.circles.feature.home.data_source

import android.content.Context
import com.futo.circles.R
import com.futo.circles.extensions.createResult
import com.futo.circles.provider.MatrixSessionProvider

class HomeDataSource(context: Context) {

    private val session = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
        context.getString(R.string.session_is_not_created)
    )
    val profileLiveData = session.userService().getUserLive(session.myUserId)

    suspend fun logOut() = createResult { session.signOutService().signOut(true) }

}