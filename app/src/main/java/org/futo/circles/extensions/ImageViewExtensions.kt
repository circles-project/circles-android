package org.futo.circles.extensions

import android.widget.ImageView
import org.futo.circles.R


fun ImageView.setIsEncryptedIcon(isEncrypted: Boolean) {
    setImageResource(if (isEncrypted) org.futo.circles.core.R.drawable.ic_lock else R.drawable.ic_lock_open)
}
