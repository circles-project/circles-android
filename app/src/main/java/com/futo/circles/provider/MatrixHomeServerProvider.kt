package com.futo.circles.provider

import android.net.Uri
import com.futo.circles.BuildConfig
import org.matrix.android.sdk.api.auth.data.HomeServerConnectionConfig

class MatrixHomeServerProvider {

    fun createHomeServerConfig() =
        HomeServerConnectionConfig
            .Builder()
            .withHomeServerUri(Uri.parse(BuildConfig.MATRIX_HOME_SERVER_URL))
            .build()

}
