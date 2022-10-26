package org.futo.circles.model

sealed class QrState(
    open val deviceId: String
)

data class QrLoading(
    override val deviceId: String,
    val isCurrentSessionVerified: Boolean
) : QrState(deviceId)

data class QrReady(
    override val deviceId: String,
    val qrText: String
) : QrState(deviceId)

data class QrSuccess(
    override val deviceId: String,
    val qrText: String
) : QrState(deviceId)

data class QrCanceled(
    override val deviceId: String,
    val qrText: String
) : QrState(deviceId)