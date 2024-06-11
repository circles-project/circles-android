package org.futo.circles.auth.model

import android.net.Uri
import org.futo.circles.core.model.CirclesRoom

data class WorkspaceTask(
    val room: CirclesRoom,
    val name: String? = null,
    val uri: Uri? = null
)
