package org.futo.circles.extensions

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.futo.circles.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar


@SuppressLint("InflateParams")
private fun Fragment.showBar(message: String, isError: Boolean, showOnActivity: Boolean) {
    val parentView = if (showOnActivity) activity?.findViewById(android.R.id.content) else view
    parentView ?: return

    val snack: Snackbar = Snackbar.make(parentView, message, Snackbar.LENGTH_LONG)
    snack.view.setBackgroundColor(Color.TRANSPARENT)

    val snackLayout = snack.view as Snackbar.SnackbarLayout
    snackLayout.setPadding(0, 0, 0, 0)

    val customSnackView = layoutInflater.inflate(
        if (isError) R.layout.view_error_snack_bar else R.layout.view_success_snack_bar,
        null
    ).apply {
        findViewById<TextView>(R.id.tvMessage)?.text = message
    }
    snackLayout.addView(customSnackView, 0)

    val layoutParams = (snack.view.layoutParams as? FrameLayout.LayoutParams)?.also { params ->
        params.gravity = Gravity.TOP
    }
    snack.view.layoutParams = layoutParams
    snack.show()
}

fun Fragment.showError(message: String, showOnActivity: Boolean = false) {
    showBar(message, true, showOnActivity)
}

fun Fragment.showSuccess(message: String, showOnActivity: Boolean = false) {
    showBar(message, false, showOnActivity)
}

fun Fragment.setEnabledViews(enabled: Boolean, viewsToExclude: List<View> = emptyList()) {
    (view?.rootView as? ViewGroup)?.setEnabledChildren(enabled, viewsToExclude)
}

fun Fragment.setSupportActionBar(toolbar: Toolbar) {
    (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
}

fun Fragment.setToolbarTitle(title: String) {
    (activity as? AppCompatActivity)?.supportActionBar?.title = title
}

fun Fragment.showDialog(
    @StringRes titleResIdRes: Int,
    @StringRes messageResId: Int? = null,
    negativeButtonVisible: Boolean = false,
    positiveAction: () -> Unit = {},
    @StringRes positiveButtonRes: Int? = null,
) {
    context?.let {
        MaterialAlertDialogBuilder(it)
            .setTitle(titleResIdRes)
            .apply { messageResId?.let { this.setMessage(it) } }
            .setPositiveButton(positiveButtonRes ?: android.R.string.ok) { dialogInterface, _ ->
                positiveAction()
                dialogInterface.dismiss()
            }
            .apply {
                if (negativeButtonVisible) {
                    this.setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                }
            }
            .show()
    }
}

fun Fragment.openCustomTabUrl(url: String) {
    context?.let {
        CustomTabsIntent.Builder().build().launchUrl(it, Uri.parse(url))
    }
}

fun Fragment.findParentNavController() = parentFragment?.parentFragment?.findNavController()

fun Fragment.onBackPressed() = activity?.onBackPressedDispatcher?.onBackPressed()