package org.futo.circles.core.room

import org.futo.circles.core.provider.MatrixSessionProvider
import javax.inject.Inject

class UserAccountDataDataSource @Inject constructor() {

    suspend fun saveSpacesTreeConfig(configMap: Map<String, String>) {
        MatrixSessionProvider.getSessionOrThrow().accountDataService().updateUserAccountData(
            SPACES_CONFIG_KEY, configMap
        )
    }

    companion object {
        private const val SPACES_CONFIG_KEY = "org.futo.circles.config"
    }
}