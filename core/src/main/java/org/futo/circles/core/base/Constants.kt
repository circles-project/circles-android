package org.futo.circles.core.base

const val FILE_PROVIDER_AUTHORITY_EXTENSION = ".provider"
const val MediaCaptionFieldKey = "caption"
const val READ_ONLY_ROLE = -10

val DEFAULT_DOMAIN = "matrix.org"

val PUSHER_APP_ID = "${CirclesAppConfig.appId}.android"

fun getPusherUrl(): String = "https://matrix.org/_matrix/push/v1/notify"

const val DEFAULT_PUSH_GATEWAY = "https://matrix.gateway.unifiedpush.org/_matrix/push/v1/notify"
