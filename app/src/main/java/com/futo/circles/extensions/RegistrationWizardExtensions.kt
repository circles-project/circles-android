package com.futo.circles.extensions

import com.futo.circles.utils.getPrivateProperty
import org.matrix.android.sdk.api.auth.registration.RegistrationWizard

fun RegistrationWizard.getPendingSignUpSessionId() =
    getPrivateProperty("pendingSessionData")
        ?.getPrivateProperty("currentSession")?.toString() ?: ""