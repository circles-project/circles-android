package org.futo.circles.core.feature.notifications

import android.content.Context
import android.os.Build
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.lifecycle.asFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.futo.circles.R
import org.futo.circles.core.utils.getAllCirclesRoomsLiveData
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import javax.inject.Inject

class ShortcutsHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val shortcutCreator: ShortcutCreator
) {

    private val isRequestPinShortcutSupported =
        ShortcutManagerCompat.isRequestPinShortcutSupported(context)
    private val maxShortcutCountPerActivity =
        ShortcutManagerCompat.getMaxShortcutCountPerActivity(context)

    fun observeRoomsAndBuildShortcuts(coroutineScope: CoroutineScope): Job {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) return Job()
        
        return getAllCirclesRoomsLiveData(listOf(Membership.JOIN)).asFlow().onEach { rooms ->
            removeDeadShortcuts(rooms.map { it.roomId })
            createShortcuts(rooms)
        }
            .flowOn(Dispatchers.Default)
            .launchIn(coroutineScope)
    }


    private fun removeDeadShortcuts(roomIds: List<String>) {
        val deadShortcutIds =
            ShortcutManagerCompat.getShortcuts(context, ShortcutManagerCompat.FLAG_MATCH_DYNAMIC)
                .map { it.id }
                .filter { !roomIds.contains(it) }

        if (deadShortcutIds.isNotEmpty()) {
            ShortcutManagerCompat.removeLongLivedShortcuts(context, deadShortcutIds)
            if (isRequestPinShortcutSupported) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                    ShortcutManagerCompat.disableShortcuts(
                        context,
                        deadShortcutIds,
                        context.getString(R.string.shortcut_disabled)
                    )
                }
            }
        }
    }

    private fun createShortcuts(rooms: List<RoomSummary>) {
        val shortcuts = rooms
            .take(maxShortcutCountPerActivity)
            .mapIndexed { index, room -> shortcutCreator.create(room, index) }
        shortcuts.forEach { shortcut ->
            ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
        }
    }

}
