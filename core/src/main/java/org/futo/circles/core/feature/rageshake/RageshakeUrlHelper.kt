package org.futo.circles.core.feature.rageshake

import org.futo.circles.core.BuildConfig
import org.futo.circles.core.base.CirclesAppConfig
import org.futo.circles.core.provider.MatrixSessionProvider

fun getRageShakeUrl(): String = "https://rageshake.${getCirclesDomain()}/bugreports/submit/"

fun getCirclesDomain(): String {
    if (BuildConfig.DEBUG) return CirclesAppConfig.usServerDomain
    val homeServerUrl = MatrixSessionProvider.currentSession?.sessionParams?.homeServerUrl ?: ""
    if (homeServerUrl.contains(CirclesAppConfig.usServerDomain)) return CirclesAppConfig.usServerDomain
    if (homeServerUrl.contains(CirclesAppConfig.euServerDomain)) return CirclesAppConfig.euServerDomain
    return CirclesAppConfig.usServerDomain
}