package org.futo.circles.feature.circles.setup

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.R
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.model.SetupCircleListItem
import org.matrix.android.sdk.api.session.getUser
import javax.inject.Inject

class SetupCirclesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val circlesLiveData = MutableLiveData(getInitialCirclesList())

    private fun getInitialCirclesList(): List<SetupCircleListItem> =
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
        return session.getUser(userId)?.notEmptyDisplayName() ?: ""
    }

    fun addCirclesCoverImage(id: Int, uri: Uri) {
        val list = circlesLiveData.value?.map {
            if (it.id == id) it.copy(coverUri = uri) else it
        } ?: emptyList()

        circlesLiveData.postValue(list)
    }
}