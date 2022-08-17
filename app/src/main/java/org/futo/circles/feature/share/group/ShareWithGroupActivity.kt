package org.futo.circles.feature.share.group

import org.futo.circles.R
import org.futo.circles.feature.circles.select.SelectCirclesFragment
import org.futo.circles.feature.photos.select.SelectRoomsFragment
import org.futo.circles.feature.share.BaseShareActivity

class ShareWithGroupActivity : BaseShareActivity() {

    override val titleResId: Int = R.string.share_with_group
    override val selectRoomsFragment: SelectRoomsFragment = SelectCirclesFragment()

}