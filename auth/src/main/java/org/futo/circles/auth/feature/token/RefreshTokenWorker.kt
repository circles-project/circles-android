package org.futo.circles.auth.feature.token

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixInstanceProvider

@HiltWorker
class RefreshTokenWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val sessionId = params.inputData.getString(SESSION_ID_PARAM_KEY) ?: run {
            WorkManager.getInstance(context).cancelWorkById(this.id)
            return Result.failure()
        }
        val result = refreshToken(sessionId)
        return if (result is Response.Success) Result.success()
        else {
            WorkManager.getInstance(context).cancelWorkById(this.id)
            Result.failure()
        }
    }

    private suspend fun refreshToken(sessionId: String) = createResult {
        MatrixInstanceProvider.matrix.authenticationService().refreshToken(sessionId)
    }

    companion object {
        const val SESSION_ID_PARAM_KEY = "sessionId"
    }
}
