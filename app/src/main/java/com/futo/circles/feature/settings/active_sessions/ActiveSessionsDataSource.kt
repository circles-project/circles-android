package com.futo.circles.feature.settings.active_sessions

import android.content.Context
import androidx.lifecycle.asFlow
import com.futo.circles.R
import com.futo.circles.core.ExpandableItemsDataSource
import com.futo.circles.model.ActiveSession
import com.futo.circles.model.ActiveSessionListItem
import com.futo.circles.model.SessionHeader
import com.futo.circles.provider.MatrixSessionProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import org.matrix.android.sdk.api.session.crypto.model.DeviceInfo

class ActiveSessionsDataSource(private val context: Context) : ExpandableItemsDataSource {

    private val session = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
        context.getString(R.string.session_is_not_created)
    )

    override val itemsWithVisibleOptionsFlow: MutableStateFlow<MutableSet<String>> =
        MutableStateFlow(mutableSetOf())


    fun getActiveSessionsFlow(): Flow<List<ActiveSessionListItem>> {
        return combine(
            session.cryptoService().getLiveMyDevicesInfo().asFlow(),
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
            cryptoDeviceInfo?.let {
                ActiveSession(
                    deviceInfo,
                    cryptoDeviceInfo,
                    sessionsWithVisibleOptions.contains(cryptoDeviceInfo.deviceId)
                )
            }
        }.sortedByDescending { it.deviceInfo.lastSeenTs }

        val sessionsList =
            mutableListOf<ActiveSessionListItem>(SessionHeader(context.getString(R.string.current_session)))
        val currentSession = devicesList.filter { it.isCurrentSession() }
        sessionsList.addAll(currentSession)
        sessionsList.add(SessionHeader(context.getString(R.string.other_sessions)))
        sessionsList.addAll(devicesList.toMutableList().apply { removeAll(currentSession) })
        return sessionsList
    }
}