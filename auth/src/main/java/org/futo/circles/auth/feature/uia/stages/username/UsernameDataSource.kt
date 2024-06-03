package org.futo.circles.auth.feature.uia.stages.username

import org.futo.circles.auth.feature.uia.UIADataSource.Companion.ENROLL_USERNAME_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.TYPE_PARAM_KEY
import org.futo.circles.auth.feature.uia.UIADataSourceProvider
import org.futo.circles.core.extensions.Response
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import javax.inject.Inject

class UsernameDataSource @Inject constructor() {

    private val uiaDataSource = UIADataSourceProvider.getDataSourceOrThrow()

    suspend fun processUsernameStage(username: String): Response<RegistrationResult> =
        uiaDataSource.performUIAStage(
            mapOf(
                TYPE_PARAM_KEY to ENROLL_USERNAME_TYPE,
                USERNAME_PARAM_KEY to username
            ), name = username
        )

    companion object {
        private const val USERNAME_PARAM_KEY = "username"
    }
}