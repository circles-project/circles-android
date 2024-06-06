package org.futo.circles.auth.model

import android.net.Uri
import org.futo.circles.core.base.list.IdEntity

data class SetupCirclesListItem(
    val name: String,
    val userId: String,
    val uri: Uri?
) : IdEntity<String> {
    override val id: String
        get() = name
}