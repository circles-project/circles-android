package org.futo.circles.core.auth

import org.futo.circles.extensions.Response

interface PasswordDataSource {
    suspend fun processPasswordStage(password: String): Response<Unit>
}