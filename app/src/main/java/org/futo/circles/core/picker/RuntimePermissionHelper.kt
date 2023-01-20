package org.futo.circles.core.picker

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.futo.circles.R

class RuntimePermissionHelper(
    private val fragment: Fragment,
    private val permission: String
) {

    private var onGranted: (() -> Unit)? = null

    fun runWithPermission(action: () -> Unit) {
        onGranted = action
        handlePermissionRequest()
    }

    fun handlePermissionRequest() {
        if (isPermissionGranted()) onGranted?.invoke()
        else requestPermissionLauncher.launch(permission)
    }

    private val requestPermissionLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) onGranted?.invoke()
            else showPermissionDenied()
        }

    private fun showPermissionDenied() {
        val context = fragment.context ?: return
        val permissionName = getPermissionName(context)
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.permission_denied_title_format, permissionName))
            .setMessage(
                context.getString(
                    R.string.permission_denied_message_format, permissionName, permissionName
                )
            )
            .setPositiveButton(R.string.open_settings) { dialogInterface, _ ->
                openAppSettings()
                dialogInterface.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }.show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = Uri.fromParts("package", fragment.requireContext().packageName, null)
        intent.data = uri
        fragment.requireActivity().startActivity(intent)
    }

    private fun isPermissionGranted() = ContextCompat.checkSelfPermission(
        fragment.requireContext(), permission
    ) == PackageManager.PERMISSION_GRANTED

    private fun getPermissionName(context: Context): String = when (permission) {
        Manifest.permission.CAMERA -> context.getString(R.string.camera)
        Manifest.permission.POST_NOTIFICATIONS -> context.getString(R.string.notifications)
        else -> permission
    }
}