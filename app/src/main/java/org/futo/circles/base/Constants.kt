package org.futo.circles.base

import org.futo.circles.BuildConfig
import org.futo.circles.core.CirclesAppConfig
import org.futo.circles.core.provider.MatrixSessionProvider


const val ROOM_BACKUP_EVENT_TYPE = "m.room.media_backup"
const val ROOM_BACKUP_DATE_MODIFIED_EVENT_TYPE = "m.room.media_backup.date_modified"

const val PUSHER_APP_ID = "${BuildConfig.APPLICATION_ID}.android"

const val READ_ONLY_ROLE = -10

fun getRageShakeUrl(): String = "https://rageshake.${getCirclesDomain()}/bugreports/submit/"
fun getPusherUrl(): String = "https://sygnal.${getCirclesDomain()}/_matrix/push/v1/notify"

const val DEFAULT_PUSH_GATEWAY = "https://matrix.gateway.unifiedpush.org/_matrix/push/v1/notify"

private fun getCirclesDomain(): String {
    if (BuildConfig.DEBUG) return CirclesAppConfig.usServerDomain
    val homeServerUrl = MatrixSessionProvider.currentSession?.sessionParams?.homeServerUrl ?: ""
    if (homeServerUrl.contains(CirclesAppConfig.usServerDomain)) return CirclesAppConfig.usServerDomain
    if (homeServerUrl.contains(CirclesAppConfig.euServerDomain)) return CirclesAppConfig.euServerDomain
    return CirclesAppConfig.usServerDomain
}

