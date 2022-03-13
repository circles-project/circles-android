package com.futo.circles.extensions

import com.futo.circles.core.CURRENT_SESSION_PROPERTY_NAME
import com.futo.circles.core.PENDING_SESSION_PROPERTY_NAME
import com.futo.circles.core.getPrivateProperty
import org.matrix.android.sdk.api.auth.registration.RegistrationWizard

fun RegistrationWizard.getPendingSignUpSessionId() =
    getPrivateProperty(PENDING_SESSION_PROPERTY_NAME)
        ?.getPrivateProperty(CURRENT_SESSION_PROPERTY_NAME)?.toString() ?: ""