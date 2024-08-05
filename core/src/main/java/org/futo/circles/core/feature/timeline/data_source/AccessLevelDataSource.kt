package org.futo.circles.core.feature.timeline.data_source

import androidx.lifecycle.asFlow
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import javax.inject.Inject

@ViewModelScoped
class AccessLevelDataSource @Inject constructor() {

    fun getAccessLevelFlow(roomId: String) =
        MatrixSessionProvider.getSessionOrThrow().getRoom(roomId)?.stateService()
            ?.getStateEventLive(EventType.STATE_ROOM_POWER_LEVELS, QueryStringValue.IsEmpty)
            ?.asFlow()
            ?.mapNotNull { it.getOrNull()?.content.toModel<PowerLevelsContent>() } ?: flowOf()
}