package org.futo.circles.auth.base

import android.content.Context
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import org.futo.circles.auth.R
import org.futo.circles.core.CirclesAppConfig

object AuthNavigator {

    fun navigateToBottomMenu(context: Context, navController: NavController) {
        navController.navigateWithUri(context, R.string.navigate_to_bottom_menu_uri)
    }

    fun navigateToSetupCircles(context: Context, navController: NavController) {
        navController.navigateWithUri(context, R.string.navigate_to_setup_circles_uri)
    }

    fun navigateToLoginStages(context: Context, navController: NavController) {
        navController.navigateWithUri(context, R.string.navigate_to_login_stages_uri)
    }

    fun navigateToSignUp(context: Context, navController: NavController) {
        navController.navigateWithUri(context, R.string.navigate_to_signup_uri)
    }

    fun navigateToSetupProfile(context: Context, navController: NavController) {
        navController.navigateWithUri(context, R.string.navigate_to_setup_profile_uri)
    }


    private fun NavController.navigateWithUri(context: Context, uriResId: Int) {
        val request = NavDeepLinkRequest.Builder
            .fromUri(context.getString(uriResId, CirclesAppConfig.appId).toUri())
            .build()
        navigate(request)
    }
}