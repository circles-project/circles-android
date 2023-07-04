package org.futo.circles.core.extensions

import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import org.matrix.android.sdk.api.extensions.tryOrNull

fun NavController.navigateSafe(directions: NavDirections) = tryOrNull { navigate(directions) }
fun NavController.navigateSafe(@IdRes resId: Int) = tryOrNull { navigate(resId) }
