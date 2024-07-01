package org.futo.circles.auth.model

import org.matrix.android.sdk.api.auth.registration.Stage

data class DomainSignupFlows(
    val domain: String,
    val freeStages: List<Stage>?,
    val subscriptionStages: List<Stage>?
)