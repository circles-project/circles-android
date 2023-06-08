package org.futo.circles.feature.timeline.data_source

import androidx.lifecycle.asFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent

class AccessLevelDataSource @AssistedInject constructor(
    @Assisted roomId: String
) {

    @AssistedFactory
    interface Factory {
        fun create(roomId: String): AccessLevelDataSource
    }

    private val room = MatrixSessionProvider.currentSession?.getRoom(roomId)

    val accessLevelFlow =
        room?.stateService()
            ?.getStateEventLive(EventType.STATE_ROOM_POWER_LEVELS, QueryStringValue.IsEmpty)
            ?.asFlow()
            ?.mapNotNull { it.getOrNull()?.content.toModel<PowerLevelsContent>() } ?: flowOf()
}