package org.futo.circles.core.extensions

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.futo.circles.core.R
import org.futo.circles.core.base.NetworkObserver
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.model.ConfirmationType
import org.matrix.android.sdk.api.extensions.tryOrNull


private const val MESSAGE_BAR_DURATION = 3500L

private fun Fragment.showDialogBar(message: String, isError: Boolean) {
    val context = (activity as? Context) ?: return
    val customSnackView = layoutInflater.inflate(
        if (isError) R.layout.view_error_snack_bar else R.layout.view_success_snack_bar,
        null
    ).apply {
        findViewById<TextView>(R.id.tvMessage)?.text = message
    }
    val dialog = AlertDialog.Builder(context)
        .setView(customSnackView)
        .create()
    dialog.window?.apply {
        setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        )
        setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 20))
        setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        setGravity(Gravity.TOP)
        clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }
    dialog.show()

    val handler = Handler(Looper.getMainLooper())
    val runnable = Runnable { tryOrNull { if (dialog.isShowing) dialog.dismiss() } }

    dialog.setOnDismissListener { handler.removeCallbacks(runnable) }
    handler.postDelayed(runnable, MESSAGE_BAR_DURATION)
}

fun Fragment.showError(message: String) {
    showDialogBar(message, true)
}

fun Fragment.showSuccess(message: String) {
    showDialogBar(message, false)
}

fun Fragment.showNoInternetConnection(): Boolean {
    val isConnected = NetworkObserver.isConnected()
    if (!isConnected) showError(getString(org.futo.circles.core.R.string.no_internet_connection))
    return !isConnected
}

fun Fragment.setEnabledViews(enabled: Boolean, viewsToExclude: List<View> = emptyList()) {
    (view as? ViewGroup)?.setEnabledChildren(enabled, viewsToExclude)
}

fun Fragment.setSupportActionBar(toolbar: Toolbar) {
    (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
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

fun Fragment.onBackPressed() = if (this is BaseFullscreenDialogFragment) dismiss() else
    activity?.onBackPressedDispatcher?.onBackPressed()

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