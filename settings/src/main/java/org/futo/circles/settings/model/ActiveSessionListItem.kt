package org.futo.circles.settings.model

import org.futo.circles.core.list.IdEntity
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.crypto.model.CryptoDeviceInfo
import org.matrix.android.sdk.api.session.crypto.model.DeviceInfo

sealed class ActiveSessionListItem : IdEntity<String>

data class SessionHeader(
    val name: String
) : ActiveSessionListItem() {
    override val id: String = name
}

data class ActiveSession(
    val deviceInfo: DeviceInfo,
    val cryptoDeviceInfo: CryptoDeviceInfo,
    val canVerify: Boolean,
    val isResetKeysVisible: Boolean,
    val isOptionsVisible: Boolean
) : ActiveSessionListItem() {
    override val id: String = cryptoDeviceInfo.deviceId

    fun isCurrentSession() =
        MatrixSessionProvider.currentSession?.sessionParams?.deviceId == cryptoDeviceInfo.deviceId

    fun isCrossSigningVerified() = cryptoDeviceInfo.trustLevel?.isCrossSigningVerified() == true
}