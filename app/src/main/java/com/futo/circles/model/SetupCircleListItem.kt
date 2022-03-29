package com.futo.circles.model

import android.net.Uri
import com.futo.circles.core.IdEntity

data class SetupCircleListItem(
    override val id: Int,
    val name: String,
    val userName: String,
    val coverUri: Uri? = null
) : IdEntity<Int>