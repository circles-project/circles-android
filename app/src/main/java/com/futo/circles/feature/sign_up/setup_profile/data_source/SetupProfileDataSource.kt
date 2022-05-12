package com.futo.circles.feature.sign_up.setup_profile.data_source

import android.content.Context
import android.net.Uri
import com.futo.circles.extensions.createResult
import com.futo.circles.extensions.getFilename
import com.futo.circles.provider.MatrixSessionProvider
import java.util.*

class SetupProfileDataSource(
    private val context: Context
) {

    suspend fun saveProfileData(profileImageUri: Uri?, displayName: String?) = createResult {
        val session = MatrixSessionProvider.currentSession

        profileImageUri?.let { uri ->
            session?.updateAvatar(session.myUserId, uri, getFileName(uri))
        }

        displayName?.let { name ->
            session?.setDisplayName(session.myUserId, name)
        }
    }

    private fun getFileName(uri: Uri) = uri.getFilename(context) ?: UUID.randomUUID().toString()

}