package org.futo.circles.auth.feature.active_sessions

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
import org.futo.circles.auth.feature.reauth.AuthConfirmationProvider
import org.futo.circles.auth.model.ActiveSession
import org.futo.circles.auth.model.ActiveSessionListItem
import org.futo.circles.auth.model.SessionHeader
import org.futo.circles.core.ExpandableItemsDataSource
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import org.matrix.android.sdk.api.session.crypto.model.DeviceInfo
import org.matrix.android.sdk.api.util.awaitCallback
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ActiveSessionsDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authConfirmationProvider: AuthConfirmationProvider
) : ExpandableItemsDataSource {

    private val session = MatrixSessionProvider.getSessionOrThrow()

    val startReAuthEventLiveData = authConfirmationProvider.startReAuthEventLiveData

    override val itemsWithVisibleOptionsFlow: MutableStateFlow<MutableSet<String>> =
        MutableStateFlow(mutableSetOf())


    fun getActiveSessionsFlow(): Flow<List<ActiveSessionListItem>> {
        return combine(
            session.cryptoService().getMyDevicesInfoLive().asFlow(),
            session.cryptoService().getLiveCryptoDeviceInfo(session.myUserId).asFlow(),
            itemsWithVisibleOptionsFlow
        ) { infoList, cryptoList, devicesWithVisibleOptions ->
            buildList(infoList, cryptoList, devicesWithVisibleOptions)
        }.flowOn(Dispatchers.IO).distinctUntilChanged()
    }


    private fun buildList(
        infoList: List<DeviceInfo>,
        cryptoList: List<CryptoDeviceInfo>,
        sessionsWithVisibleOptions: Set<String>
    ): List<ActiveSessionListItem> {
        val devicesList = infoList.mapNotNull { deviceInfo ->
            val cryptoDeviceInfo = cryptoList.firstOrNull { it.deviceId == deviceInfo.deviceId }
            cryptoDeviceInfo?.let { deviceInfo to it }
        }.sortedByDescending { it.first.lastSeenTs }

        val currentSession =
            devicesList.firstOrNull { it.second.deviceId == MatrixSessionProvider.currentSession?.sessionParams?.deviceId }
                ?: return emptyList()
        val otherSessions = devicesList.toMutableList().apply { remove(currentSession) }
            .filter { !isSessionInactive(it.first.lastSeenTs) }
        val isCurrentSessionVerified =
            currentSession.second.trustLevel?.isCrossSigningVerified() == true

        val sessionsList =
            mutableListOf<ActiveSessionListItem>(SessionHeader(context.getString(R.string.current_session)))
        sessionsList.add(
            ActiveSession(
                deviceInfo = currentSession.first,
                cryptoDeviceInfo = currentSession.second,
                canVerify = !isCurrentSessionVerified && otherSessions.isNotEmpty(),
                isResetKeysVisible = !isCurrentSessionVerified,
                isOptionsVisible = sessionsWithVisibleOptions.contains(currentSession.second.deviceId)
            )
        )
        if (otherSessions.isNotEmpty()) {
            sessionsList.add(SessionHeader(context.getString(R.string.other_sessions)))
            sessionsList.addAll(otherSessions.map {
                ActiveSession(
                    deviceInfo = it.first,
                    cryptoDeviceInfo = it.second,
                    canVerify = isCurrentSessionVerified && it.second.trustLevel?.isCrossSigningVerified() != true,
                    isResetKeysVisible = false,
                    isOptionsVisible = sessionsWithVisibleOptions.contains(it.second.deviceId)
                )
            }
            )
        }
        return sessionsList
    }

    suspend fun removeSession(deviceId: String): Response<Unit> = createResult {
        awaitCallback {
            session.cryptoService().deleteDevice(deviceId, authConfirmationProvider, it)
        }
    }

    suspend fun resetKeysToEnableCrossSigning(): Response<Unit> = createResult {
        awaitCallback {
            session.cryptoService().crossSigningService()
                .initializeCrossSigning(authConfirmationProvider, it)
        }
    }

    private fun isSessionInactive(lastSeenTsMillis: Long?): Boolean =
        if (lastSeenTsMillis == null || lastSeenTsMillis <= 0) {
            false
        } else {
            val diffMilliseconds = System.currentTimeMillis() - lastSeenTsMillis
            diffMilliseconds >= TimeUnit.DAYS.toMillis(SESSION_IS_MARKED_AS_INACTIVE_AFTER_DAYS)
        }

    companion object {
        private const val SESSION_IS_MARKED_AS_INACTIVE_AFTER_DAYS = 30L
    }

}