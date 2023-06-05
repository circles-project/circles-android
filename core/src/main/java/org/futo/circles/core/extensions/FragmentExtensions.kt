package org.futo.circles.core.extensions

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.futo.circles.core.R
import org.futo.circles.core.model.ConfirmationType

private const val SNACK_BAR_DURATION = 3500

@SuppressLint("InflateParams")
private fun Fragment.showBar(message: String, isError: Boolean, showOnActivity: Boolean) {
    val parentView = if (showOnActivity) activity?.findViewById(android.R.id.content) else view
    parentView ?: return

    val snack: Snackbar = Snackbar.make(parentView, message, SNACK_BAR_DURATION)
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

fun Fragment.setToolbarSubTitle(title: String) {
    (activity as? AppCompatActivity)?.supportActionBar?.subtitle = title
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

fun Fragment.onBackPressed() = activity?.onBackPressedDispatcher?.onBackPressed()

fun Fragment.withConfirmation(type: ConfirmationType, action: () -> Unit) {
    showDialog(
        titleResIdRes = type.titleRes,
        messageResId = type.messageRes,
        positiveButtonRes = type.positiveButtonRes,
        negativeButtonVisible = true,
        positiveAction = action
    )
}

fun Fragment.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val uri = Uri.fromParts("package", requireContext().applicationContext.packageName, null)
    intent.data = uri
    requireActivity().startActivity(intent)
}

fun Fragment.openNotificationSettings() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val intent = Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().applicationContext.packageName)
        }
        requireActivity().startActivity(intent)
    } else openAppSettings()
}

fun Fragment.getAllChildFragments(): List<Fragment> {
    return listOf(this) + childFragmentManager.fragments.map { it.getAllChildFragments() }.flatten()
}