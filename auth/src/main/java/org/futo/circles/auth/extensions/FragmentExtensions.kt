package org.futo.circles.auth.extensions

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment

fun Fragment.openCustomTabUrl(url: String) {
    context?.let {
        CustomTabsIntent.Builder().build().launchUrl(it, Uri.parse(url))
    }
}