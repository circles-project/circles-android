package org.futo.circles.settings.model

sealed class QrState

data object QrLoading : QrState()

data class QrReady(val qrText: String) : QrState()

data object QrSuccess : QrState()

data object QrCanceled : QrState()