package org.futo.circles.auth.model

sealed class QrState

data class QrLoading(val deviceId: String) : QrState()

data class QrReady(
    val qrText: String
) : QrState()

data object QrSuccess : QrState()

data object QrCanceled : QrState()