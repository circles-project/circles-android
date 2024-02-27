package org.futo.circles.auth.feature.sign_up.username

import androidx.lifecycle.MutableLiveData
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.REGISTRATION_USERNAME_TYPE
import org.futo.circles.auth.feature.uia.UIADataSource.Companion.TYPE_PARAM_KEY
import org.futo.circles.auth.feature.uia.UIADataSourceProvider
import org.futo.circles.core.extensions.Response
import org.matrix.android.sdk.api.auth.registration.RegistrationResult
import javax.inject.Inject

class UsernameDataSource @Inject constructor() {

    private val uiaDataSource = UIADataSourceProvider.getDataSourceOrThrow()

    val domainLiveData = MutableLiveData(uiaDataSource.domain)

    suspend fun processUsernameStage(username: String): Response<RegistrationResult> =
        uiaDataSource.performUIAStage(
            mapOf(
                TYPE_PARAM_KEY to REGISTRATION_USERNAME_TYPE,
                USERNAME_PARAM_KEY to username
            ), name = username
        )

    companion object {
        private const val USERNAME_PARAM_KEY = "username"
    }
}