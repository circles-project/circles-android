package org.futo.circles.settings.feature.active_sessions

import android.content.Context
import androidx.lifecycle.asFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import org.futo.circles.auth.R
import org.futo.circles.auth.feature.uia.flow.reauth.AuthConfirmationProvider
import org.futo.circles.core.base.ExpandableItemsDataSource
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.settings.model.ActiveSession
import org.futo.circles.settings.model.ActiveSessionListItem
import org.futo.circles.settings.model.SessionHeader
import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import javax.inject.Inject

class ActiveSessionsDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authConfirmationProvider: AuthConfirmationProvider
) : ExpandableItemsDataSource {

    private val session = MatrixSessionProvider.getSessionOrThrow()

    val startReAuthEventLiveData = authConfirmationProvider.startReAuthEventLiveData

    override val itemsWithVisibleOptionsFlow: MutableStateFlow<MutableSet<String>> =
        MutableStateFlow(mutableSetOf())


    suspend fun refreshDevicesList() {
        session.cryptoService().downloadKeysIfNeeded(listOf(session.myUserId), true)
    }

    fun getActiveSessionsFlow(): Flow<List<ActiveSessionListItem>> {
        return combine(
            session.cryptoService().getLiveCryptoDeviceInfo(session.myUserId).asFlow(),
            itemsWithVisibleOptionsFlow
        ) { cryptoList, devicesWithVisibleOptions ->
            buildList(cryptoList, devicesWithVisibleOptions)
        }.flowOn(Dispatchers.IO).distinctUntilChanged()
    }


    private fun buildList(
        cryptoList: List<CryptoDeviceInfo>,
        sessionsWithVisibleOptions: Set<String>
    ): List<ActiveSessionListItem> {
        val currentSession = cryptoList.firstOrNull {
            it.deviceId == MatrixSessionProvider.currentSession?.sessionParams?.deviceId
        } ?: return emptyList()

        val otherSessions = cryptoList.toMutableList().apply { remove(currentSession) }
            .sortedByDescending { it.firstTimeSeenLocalTs }

        val isCurrentSessionVerified =
            currentSession.trustLevel?.isCrossSigningVerified() == true

        val sessionsList =
            mutableListOf<ActiveSessionListItem>(SessionHeader(context.getString(R.string.current_session)))
        sessionsList.add(
            ActiveSession(
                cryptoDeviceInfo = currentSession,
                canVerify = !isCurrentSessionVerified && otherSessions.isNotEmpty(),
                isResetKeysVisible = !isCurrentSessionVerified,
                isOptionsVisible = sessionsWithVisibleOptions.contains(currentSession.deviceId)
            )
        )
        if (otherSessions.isNotEmpty()) {
            sessionsList.add(SessionHeader(context.getString(R.string.other_sessions)))
            sessionsList.addAll(otherSessions.mapNotNull {
                if (!it.isDehydrated) {
                    ActiveSession(
                        cryptoDeviceInfo = it,
                        canVerify = isCurrentSessionVerified && it.trustLevel?.isCrossSigningVerified() != true,
                        isResetKeysVisible = false,
                        isOptionsVisible = sessionsWithVisibleOptions.contains(it.deviceId)
                    )
                } else null
            }
            )
        }
        return sessionsList
    }

    suspend fun removeSession(deviceId: String): Response<Unit> = createResult {
        session.cryptoService().deleteDevice(deviceId, authConfirmationProvider)
    }

    suspend fun resetKeysToEnableCrossSigning(): Response<Unit> = createResult {
        session.cryptoService().crossSigningService()
            .initializeCrossSigning(authConfirmationProvider)
    }

}