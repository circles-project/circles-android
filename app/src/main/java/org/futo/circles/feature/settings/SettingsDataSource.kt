package org.futo.circles.feature.settings

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.futo.circles.R
import org.futo.circles.core.matrix.auth.AuthConfirmationProvider
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult
import org.futo.circles.feature.settings.change_password.ChangePasswordDataSource
import org.futo.circles.model.LoadingData
import org.futo.circles.provider.MatrixSessionProvider
import java.io.File

class SettingsDataSource(
    private val context: Context,
    private val changePasswordDataSource: ChangePasswordDataSource,
    private val authConfirmationProvider: AuthConfirmationProvider
) {

    private val session = MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
        context.getString(R.string.session_is_not_created)
    )
    val passPhraseLoadingLiveData = changePasswordDataSource.passPhraseLoadingLiveData
    val startReAuthEventLiveData = authConfirmationProvider.startReAuthEventLiveData
    val profileLiveData = session.userService().getUserLive(session.myUserId)

    val loadingLiveData = MutableLiveData<LoadingData>()
    private val loadingData = LoadingData(total = 0)

    suspend fun logOut(logoutFromHomeServer: Boolean = true) = createResult {
        loadingLiveData.postValue(
            loadingData.apply {
                messageId = if (logoutFromHomeServer) R.string.log_out else R.string.switch_user
                isLoading = true
            }
        )
        session.signOutService().signOut(true)
        loadingLiveData.postValue(loadingData.apply { isLoading = false })
    }

    suspend fun switchUser() = logOut(false)

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