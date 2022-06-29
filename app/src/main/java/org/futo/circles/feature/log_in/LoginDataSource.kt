package org.futo.circles.feature.log_in

import android.content.Context
import android.net.Uri
import org.futo.circles.R
import org.futo.circles.core.HomeServerUtils
import org.futo.circles.core.matrix.pass_phrase.restore.RestorePassPhraseDataSource
import org.futo.circles.core.matrix.room.CoreSpacesTreeBuilder
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.provider.MatrixInstanceProvider
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig
import org.matrix.android.sdk.api.session.Session

class LoginDataSource(
    private val context: Context,
    private val restorePassPhraseDataSource: RestorePassPhraseDataSource,
    private val coreSpacesTreeBuilder: CoreSpacesTreeBuilder
) {

    val passPhraseLoadingLiveData = restorePassPhraseDataSource.loadingLiveData

    suspend fun logIn(name: String, password: String): Response<Session> = createResult {
        val session = MatrixInstanceProvider.matrix.authenticationService().directAuthentication(
            homeServerConnectionConfig = buildHomeServerConfig(name),
            matrixId = name,
            password = password,
            initialDeviceName = context.getString(
                R.string.initial_device_name,
                context.getString(R.string.app_name)
            )
        )
        MatrixSessionProvider.awaitForSessionSync(session)
    }

    suspend fun restoreKeys(password: String) = createResult {
        restorePassPhraseDataSource.restoreKeysWithPassPhase(password)
    }

    suspend fun getEncryptionAlgorithm(): String? {
        val algorithmResult = createResult { restorePassPhraseDataSource.getEncryptionAlgorithm() }

       return when(algorithmResult){
            is Response.Error -> null
            is Response.Success -> algorithmResult.data
        }
    }

    suspend fun createSpacesTree() = createResult {
        coreSpacesTreeBuilder.createCoreSpacesTree()
    }

    fun isCirclesTreeCreated() = coreSpacesTreeBuilder.isCirclesHierarchyCreated()

    private fun buildHomeServerConfig(userName: String): HomeServerConnectionConfig {
        val url = HomeServerUtils.getHomeServerUrlFromUserName(userName)
        return HomeServerConnectionConfig
            .Builder()
            .withHomeServerUri(Uri.parse(url))
            .build()
    }
}