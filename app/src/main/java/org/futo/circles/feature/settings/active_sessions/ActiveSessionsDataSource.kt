package org.futo.circles.feature.settings.active_sessions

import android.content.Context
import androidx.lifecycle.asFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.futo.circles.R
import org.futo.circles.core.ExpandableItemsDataSource
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.matrix.auth.AuthConfirmationProvider
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.model.ActiveSession
import org.futo.circles.model.ActiveSessionListItem
import org.futo.circles.model.SessionHeader
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import org.matrix.android.sdk.api.session.crypto.model.DeviceInfo
import org.matrix.android.sdk.api.session.crypto.verification.VerificationMethod
import org.matrix.android.sdk.api.util.awaitCallback

class ActiveSessionsDataSource(
    private val context: Context,
    private val authConfirmationProvider: AuthConfirmationProvider
) : ExpandableItemsDataSource {

    private val session = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
        context.getString(R.string.session_is_not_created)
    )

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
        val isCurrentSessionVerified =
            currentSession.second.trustLevel?.isCrossSigningVerified() == true
        val isOtherSessionVerified =
            otherSessions.firstOrNull { it.second.trustLevel?.isCrossSigningVerified() == true } != null

        val sessionsList =
            mutableListOf<ActiveSessionListItem>(SessionHeader(context.getString(R.string.current_session)))
        sessionsList.add(
            ActiveSession(
                deviceInfo = currentSession.first,
                cryptoDeviceInfo = currentSession.second,
                canVerify = !isCurrentSessionVerified && isOtherSessionVerified,
                canEnableCrossSigning = !isCurrentSessionVerified && !isOtherSessionVerified,
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
                    canEnableCrossSigning = false,
                    isOptionsVisible = sessionsWithVisibleOptions.contains(it.second.deviceId)
                )
            }
            )
        }
        return sessionsList
    }

    suspend fun verifyDevice(deviceId: String): Response<Unit> {
        var response: Response<Unit>? = null
        if (session.cryptoService().getMyDevice().trustLevel?.isCrossSigningVerified() == true) {
            response = verifyCrossSigning(deviceId)
        }
        verifyLocally(deviceId)
        return response ?: Response.Success(Unit)
    }

    suspend fun removeSession(deviceId: String): Response<Unit> = createResult {
        awaitCallback {
            session.cryptoService().deleteDevice(deviceId, authConfirmationProvider, it)
        }
    }

    suspend fun enableCrossSigning(): Response<Unit> = createResult {
        awaitCallback {
            session.cryptoService().crossSigningService().initializeCrossSigning(authConfirmationProvider, it)
        }
    }

}