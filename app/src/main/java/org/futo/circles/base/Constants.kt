package org.futo.circles.base

import org.futo.circles.BuildConfig
import org.futo.circles.core.getCirclesDomain


const val PUSHER_APP_ID = "${BuildConfig.APPLICATION_ID}.android"

const val READ_ONLY_ROLE = -10

fun getPusherUrl(): String = "https://sygnal.${getCirclesDomain()}/_matrix/push/v1/notify"

const val DEFAULT_PUSH_GATEWAY = "https://matrix.gateway.unifiedpush.org/_matrix/push/v1/notify"

const val CIRCULI_INVITE_URL = "https://circu.li/invite/"

