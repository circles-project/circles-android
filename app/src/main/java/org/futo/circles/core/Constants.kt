package org.futo.circles.core

import org.futo.circles.BuildConfig
import org.futo.circles.provider.MatrixSessionProvider

const val FILE_PROVIDER_AUTHORITY_EXTENSION = ".provider"

const val REGISTRATION_TOKEN_TYPE = "m.login.registration_token"
const val REGISTRATION_SUBSCRIPTION_TYPE = "org.futo.subscription.google_play"
const val REGISTRATION_EMAIL_REQUEST_TOKEN_TYPE = "m.enroll.email.request_token"
const val REGISTRATION_EMAIL_SUBMIT_TOKEN_TYPE = "m.enroll.email.submit_token"
const val REGISTRATION_USERNAME_TYPE = "m.enroll.username"
const val REGISTRATION_PASSWORD_TYPE = "m.enroll.password"
const val REGISTRATION_BSSPEKE_OPRF_TYPE = "m.enroll.bsspeke-ecc.oprf"
const val REGISTRATION_BSSPEKE_SAVE_TYPE = "m.enroll.bsspeke-ecc.save"

const val LOGIN_PASSWORD_TYPE = "m.login.password"
const val DIRECT_LOGIN_PASSWORD_TYPE = "m.login.password.direct"
const val LOGIN_BSSPEKE_OPRF_TYPE = "m.login.bsspeke-ecc.oprf"
const val LOGIN_BSSPEKE_VERIFY_TYPE = "m.login.bsspeke-ecc.verify"

const val ROOM_BACKUP_EVENT_TYPE = "m.room.media_backup"

const val LOGIN_PASSWORD_USER_ID_TYPE = "m.id.user"

const val DEFAULT_USER_PREFIX = "@notices:"

const val SYSTEM_NOTICES_TAG = "m.server_notice"

const val TYPE_PARAM_KEY = "type"

const val CREATE_ROOM_DELAY = 1000L

const val PUSHER_APP_ID = "${BuildConfig.APPLICATION_ID}.android"

const val READ_ONLY_ROLE = -10

fun getRageShakeUrl(): String = "https://rageshake.${getCirclesDomain()}/bugreports/submit/"
fun getPusherUrl(): String = "https://sygnal.${getCirclesDomain()}/_matrix/push/v1/notify"

const val DEFAULT_PUSH_GATEWAY = "https://matrix.gateway.unifiedpush.org/_matrix/push/v1/notify"

private fun getCirclesDomain(): String {
    if (BuildConfig.DEBUG) return BuildConfig.US_SERVER_DOMAIN
    val homeServerUrl = MatrixSessionProvider.currentSession?.sessionParams?.homeServerUrl ?: ""
    if (homeServerUrl.contains(BuildConfig.US_SERVER_DOMAIN)) return BuildConfig.US_SERVER_DOMAIN
    if (homeServerUrl.contains(BuildConfig.EU_SERVER_DOMAIN)) return BuildConfig.EU_SERVER_DOMAIN
    return BuildConfig.US_SERVER_DOMAIN
}

