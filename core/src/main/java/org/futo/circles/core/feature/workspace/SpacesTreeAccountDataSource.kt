package org.futo.circles.core.feature.workspace

import org.futo.circles.core.model.CIRCLES_SPACE_ACCOUNT_DATA_KEY
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getJoinedRoomById
import javax.inject.Inject

class SpacesTreeAccountDataSource @Inject constructor() {

    private fun getSpacesTreeConfig() =
        MatrixSessionProvider.currentSession?.accountDataService()
            ?.getUserAccountDataEvent(SPACES_CONFIG_KEY)?.content ?: emptyMap()

    suspend fun updateSpacesConfigAccountData(key: String, roomId: String) {
        val currentConfig = getSpacesTreeConfig().toMutableMap()
        currentConfig[key] = roomId
        saveSpacesTreeConfig(currentConfig)
    }

    fun getRoomIdByKey(key: String) = getSpacesTreeConfig()[key]?.toString()

    fun getJoinedCirclesIds(): List<String> {
        val circlesSpaceId = getRoomIdByKey(CIRCLES_SPACE_ACCOUNT_DATA_KEY) ?: return emptyList()
        val ids = getJoinedRoomById(circlesSpaceId)?.roomSummary()?.spaceChildren
            ?.map { it.childRoomId }
            ?.filter { getJoinedRoomById(it) != null }
        return ids ?: emptyList()
    }

    private suspend fun saveSpacesTreeConfig(configMap: Map<String, Any>) {
        MatrixSessionProvider.getSessionOrThrow().accountDataService()
            .updateUserAccountData(SPACES_CONFIG_KEY, configMap)
    }

    companion object {
        private const val SPACES_CONFIG_KEY = "org.futo.circles.config"
    }
}