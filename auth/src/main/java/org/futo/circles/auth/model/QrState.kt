package org.futo.circles.auth.model

sealed class QrState

data object QrLoading : QrState()

data class QrReady(val qrText: String) : QrState()

data object QrSuccess : QrState()

data object QrCanceled : QrState()