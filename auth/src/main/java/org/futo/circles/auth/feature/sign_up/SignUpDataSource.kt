package org.futo.circles.auth.feature.sign_up

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.feature.uia.UIADataSource
import org.futo.circles.auth.feature.uia.UIADataSourceProvider
import org.futo.circles.auth.model.UIAFlowType
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixInstanceProvider
import org.futo.circles.core.utils.HomeServerUtils.buildHomeServerConfigFromDomain
import org.matrix.android.sdk.api.auth.registration.Stage
import javax.inject.Inject

class SignUpDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val uiaFactory: UIADataSource.Factory
) {

    suspend fun startNewRegistration(domain: String) = createResult {
        val homeServerUrl = initAuthServiceForDomain(domain)
        val stages = getAuthStages() ?: throw IllegalArgumentException(
            context.getString(R.string.new_accounts_not_available)
        )
        val uiaDataSource = UIADataSourceProvider.create(UIAFlowType.Signup, uiaFactory)
        uiaDataSource.setHomeServerUrl(homeServerUrl)
        uiaDataSource.startUIAStages(stages, domain)
    }

    private suspend fun initAuthServiceForDomain(domain: String): String {
        val service = MatrixInstanceProvider.matrix.authenticationService()
        service.cancelPendingLoginOrRegistration()
        return service.initiateAuth(buildHomeServerConfigFromDomain(domain))
    }

    private suspend fun getAuthStages(): List<Stage>? {
        val flows = MatrixInstanceProvider.matrix.authenticationService().getRegistrationWizard()
            .getAllRegistrationFlows()
        return flows.firstOrNull()
    }

}