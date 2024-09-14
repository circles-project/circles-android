package org.futo.circles.auth.feature.setup.profile

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.feature.uia.flow.reauth.AuthConfirmationProvider
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getFilename
import org.futo.circles.core.provider.MatrixSessionProvider
import java.util.UUID
import javax.inject.Inject

class SetupProfileDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authConfirmationProvider: AuthConfirmationProvider
) {

    private val session by lazy { MatrixSessionProvider.getSessionOrThrow() }
    val startReAuthEventLiveData = authConfirmationProvider.startReAuthEventLiveData

    fun getUserData() = session.userService().getUser(session.myUserId)

    suspend fun saveProfileData(profileImageUri: Uri?, displayName: String?) = createResult {
        profileImageUri?.let { uri ->
            session.profileService().updateAvatar(session.myUserId, uri, getFileName(uri))
        }

        displayName?.let { name ->
            session.profileService().setDisplayName(session.myUserId, name)
        }
    }

    suspend fun addEmailUIA() = createResult {
        session.accountService().changeEmailStages(authConfirmationProvider)
    }

    suspend fun deleteEmailUIA(email: String) = createResult {
        session.profileService().deleteEmailThreePidStages(email, authConfirmationProvider)
    }

    fun isNameChanged(newName: String) =
        session.userService().getUser(session.myUserId)?.displayName != newName

    private fun getFileName(uri: Uri) = uri.getFilename(context) ?: UUID.randomUUID().toString()

}