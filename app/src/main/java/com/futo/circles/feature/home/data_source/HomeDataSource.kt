package com.futo.circles.feature.home.data_source

import com.futo.circles.extensions.createResult
import com.futo.circles.provider.MatrixSessionProvider

class HomeDataSource {

    private val session = MatrixSessionProvider.currentSession

    suspend fun logOut() = createResult { session?.signOutService()?.signOut(true) }

}