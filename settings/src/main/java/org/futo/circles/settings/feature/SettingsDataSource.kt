package org.futo.circles.settings.feature

import android.content.Context
import com.bumptech.glide.Glide
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.futo.circles.auth.feature.reauth.AuthConfirmationProvider
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.settings.R
import org.futo.circles.settings.feature.change_password.ChangePasswordDataSource
import java.io.File
import javax.inject.Inject

class SettingsDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val changePasswordDataSource: ChangePasswordDataSource,
    private val authConfirmationProvider: AuthConfirmationProvider
) {

    private val session = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
        context.getString(R.string.session_is_not_created)
    )
    val passPhraseLoadingLiveData = changePasswordDataSource.passPhraseLoadingLiveData
    val startReAuthEventLiveData = authConfirmationProvider.startReAuthEventLiveData
    val profileLiveData = session.userService().getUserLive(session.myUserId)

    suspend fun deactivateAccount(): Response<Unit> = createResult {
        session.accountService().deactivateAccount(false, authConfirmationProvider)
    }

    suspend fun changePasswordUIA() =
        changePasswordDataSource.changePasswordUIA(authConfirmationProvider)

    suspend fun createNewBackupIfNeeded() =
        changePasswordDataSource.createNewBackupInNeeded(authConfirmationProvider.getNewChangedPassword())

    suspend fun clearCache() {
        withContext(Dispatchers.Main) {
            Glide.get(context).clearMemory()
            session.fileService().clearCache()
        }
        withContext(Dispatchers.IO) {
            Glide.get(context).clearDiskCache()
            recursiveActionOnFile(context.cacheDir, ::deleteAction)
            session.clearCache()
        }
    }

    private fun deleteAction(file: File): Boolean {
        if (file.exists()) return file.delete()
        return true
    }

    private fun recursiveActionOnFile(file: File, action: (file: File) -> Boolean): Boolean {
        if (file.isDirectory) {
            file.list()?.forEach {
                val result = recursiveActionOnFile(File(file, it), action)
                if (!result) return false
            }
        }
        return action.invoke(file)
    }
}