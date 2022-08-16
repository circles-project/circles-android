package org.futo.circles.feature.share.circle

import org.futo.circles.R
import org.futo.circles.feature.share.BaseShareActivity

class ShareWithCircleActivity : BaseShareActivity() {
    override val titleResId: Int = R.string.share_with_circle

    override fun getSelectedRoomsIds(): List<String> = emptyList()

}