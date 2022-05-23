package com.futo.circles.feature.settings.change_password

import com.futo.circles.extensions.createResult
import com.futo.circles.provider.MatrixSessionProvider

class ChangePasswordDataSource {


    suspend fun changePassword(oldPassword: String, newPassword: String) = createResult {
        MatrixSessionProvider.currentSession?.accountService()
            ?.changePassword(oldPassword, newPassword)
    }
}