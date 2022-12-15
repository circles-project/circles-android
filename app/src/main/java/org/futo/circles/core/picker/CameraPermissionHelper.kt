package org.futo.circles.core.picker

import android.Manifest
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

class CameraPermissionHelper(private val fragment: Fragment) {

    private var onGranted: (() -> Unit)? = null

    fun runWithCameraPermission(action: () -> Unit) {
        onGranted = action
        when {
            isCameraPermissionGranted() -> onGranted?.invoke()
            shouldShowCameraRationale() -> showPermissionDeny()
            else -> requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private val requestCameraPermissionLauncher =
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
        fragment.requireActivity(),
        Manifest.permission.CAMERA
    )

    private fun isCameraPermissionGranted() = ContextCompat.checkSelfPermission(
        fragment.requireContext(),
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED
}