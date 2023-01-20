package org.futo.circles.core.picker

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.futo.circles.extensions.withConfirmation
import org.futo.circles.model.ConfirmationType

class RuntimePermissionHelper(
    private val fragment: Fragment,
    private val permission: String
) {

    private var onGranted: (() -> Unit)? = null

    fun runWithPermission(action: () -> Unit) {
        onGranted = action
        when {
            isPermissionGranted() -> onGranted?.invoke()
            shouldShowCameraRationale() -> showPermissionDeny()
            else -> requestPermissionLauncher.launch(permission)
        }
    }

    private val requestPermissionLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) onGranted?.invoke()
            else showPermissionDeny()
        }

    private fun showPermissionDeny() {
        fragment.withConfirmation(ConfirmationType.PERMISSION_DENIED) { openAppSettings() }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = Uri.fromParts("package", fragment.requireContext().packageName, null)
        intent.data = uri
        fragment.requireActivity().startActivity(intent)
    }

    private fun shouldShowCameraRationale() = ActivityCompat.shouldShowRequestPermissionRationale(
        fragment.requireActivity(), permission
    )

    private fun isPermissionGranted() = ContextCompat.checkSelfPermission(
        fragment.requireContext(), permission
    ) == PackageManager.PERMISSION_GRANTED
}