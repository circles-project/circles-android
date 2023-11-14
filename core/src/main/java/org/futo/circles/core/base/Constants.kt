package org.futo.circles.core.base

import org.futo.circles.core.BuildConfig
import org.futo.circles.core.provider.MatrixSessionProvider

const val FILE_PROVIDER_AUTHORITY_EXTENSION = ".provider"
const val MediaCaptionFieldKey = "caption"
const val READ_ONLY_ROLE = -10

fun getCirclesDomain(): String {
    if (BuildConfig.DEBUG) return CirclesAppConfig.usServerDomain
    val homeServerUrl = MatrixSessionProvider.currentSession?.sessionParams?.homeServerUrl ?: ""
    if (homeServerUrl.contains(CirclesAppConfig.usServerDomain)) return CirclesAppConfig.usServerDomain
    if (homeServerUrl.contains(CirclesAppConfig.euServerDomain)) return CirclesAppConfig.euServerDomain
    return CirclesAppConfig.usServerDomain
}
