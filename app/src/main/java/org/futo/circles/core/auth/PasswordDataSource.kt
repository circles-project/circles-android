package org.futo.circles.core.auth

import org.futo.circles.extensions.Response

interface PasswordDataSource {
    fun getMinimumPasswordLength(): Int
    suspend fun processPasswordStage(password: String): Response<Unit>
}