package org.futo.circles.feature.share.group

import org.futo.circles.R
import org.futo.circles.feature.share.BaseShareActivity

class ShareWithGroupActivity : BaseShareActivity() {
    
    override val titleResId: Int = R.string.share_with_group
    override fun getSelectedRoomsIds(): List<String> = emptyList()

}