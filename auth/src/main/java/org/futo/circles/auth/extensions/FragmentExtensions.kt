package org.futo.circles.auth.extensions

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import org.matrix.android.sdk.api.extensions.tryOrNull

fun Fragment.openCustomTabUrl(url: String) {
    context?.let {
        tryOrNull { CustomTabsIntent.Builder().build().launchUrl(it, Uri.parse(url)) }
    }
}