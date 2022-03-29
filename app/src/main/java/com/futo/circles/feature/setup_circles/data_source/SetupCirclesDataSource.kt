package com.futo.circles.feature.setup_circles.data_source

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.futo.circles.R
import com.futo.circles.model.SetupCircleListItem
import com.futo.circles.provider.MatrixSessionProvider

class SetupCirclesDataSource(
    private val context: Context
) {

    val circlesLiveData = MutableLiveData<List<SetupCircleListItem>>(getInitialCirclesList())

    private fun getInitialCirclesList() =
        context.resources.getStringArray(R.array.setup_circles_list).mapIndexed { i, name ->
            SetupCircleListItem(
                id = i,
                name = name,
                userName = getUserName()
            )
        }

    private fun getUserName(): String {
        val session = MatrixSessionProvider.currentSession
        val userId = session?.myUserId ?: return ""
        return session.getUser(userId)?.displayName ?: ""
    }
}