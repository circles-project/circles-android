package org.futo.circles.feature.settings.change_password

import org.futo.circles.extensions.createResult
import org.futo.circles.provider.MatrixSessionProvider

class ChangePasswordDataSource {


    suspend fun changePassword(oldPassword: String, newPassword: String) = createResult {
        MatrixSessionProvider.currentSession?.accountService()
            ?.changePassword(oldPassword, newPassword)
    }
}