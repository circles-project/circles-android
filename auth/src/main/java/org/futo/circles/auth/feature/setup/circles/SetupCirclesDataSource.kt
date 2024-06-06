package org.futo.circles.auth.feature.setup.circles

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.model.SetupCirclesListItem
import javax.inject.Inject

class SetupCirclesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {


    fun getInitialCirclesList(): List<SetupCirclesListItem> = listOf(
        SetupCirclesListItem(context.getString(R.string.family)),
        SetupCirclesListItem(context.getString(R.string.friends))
    )

}