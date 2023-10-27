package org.futo.circles.core.feature.rageshake

import org.futo.circles.core.base.getCirclesDomain

fun getRageShakeUrl(): String = "https://rageshake.${getCirclesDomain()}/bugreports/submit/"
