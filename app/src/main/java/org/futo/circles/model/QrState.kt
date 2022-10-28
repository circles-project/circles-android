package org.futo.circles.model

sealed class QrState

data class QrLoading(
    val deviceId: String,
    val isCurrentSessionVerified: Boolean
) : QrState()

data class QrReady(
    val qrText: String
) : QrState()

object QrSuccess : QrState()

data class QrCanceled(
    val reason: String
) : QrState()