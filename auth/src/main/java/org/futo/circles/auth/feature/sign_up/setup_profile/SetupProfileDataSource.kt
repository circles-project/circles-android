package org.futo.circles.auth.feature.sign_up.setup_profile

import android.content.Context
import android.net.Uri
import org.futo.circles.auth.R
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.getFilename
import org.futo.circles.core.provider.MatrixSessionProvider
import java.util.UUID

class SetupProfileDataSource(
    private val context: Context
) {

    private val session by lazy {
        MatrixSessionProvider.currentSession ?: throw IllegalArgumentException(
            context.getString(R.string.session_is_not_created)
        )
    }

    fun getUserData() = session.userService().getUser(session.myUserId)

    fun getThreePidData() = session.profileService().getThreePids()

    suspend fun saveProfileData(profileImageUri: Uri?, displayName: String?) = createResult {
        profileImageUri?.let { uri ->
            session.profileService().updateAvatar(session.myUserId, uri, getFileName(uri))
        }

        displayName?.let { name ->
            session.profileService().setDisplayName(session.myUserId, name)
        }
    }

    fun isNameChanged(newName: String) =
        session.userService().getUser(session.myUserId)?.displayName != newName

    private fun getFileName(uri: Uri) = uri.getFilename(context) ?: UUID.randomUUID().toString()

}