package org.futo.circles.auth.base

import org.futo.circles.core.extensions.Response

interface PasswordDataSource {
    suspend fun processPasswordStage(password: String): Response<Unit>
}