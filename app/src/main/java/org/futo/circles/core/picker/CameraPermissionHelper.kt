package org.futo.circles.core.picker

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import org.futo.circles.R
import org.futo.circles.extensions.showDialog

class CameraPermissionHelper(private val fragment: Fragment) {

    private var onGranted: (() -> Unit)? = null

    fun runWithCameraPermission(action: () -> Unit) {
        onGranted = action
        if (checkForCameraPermissionDeny()) return
        requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private val requestCameraPermissionLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                onGranted?.invoke()
            } else {
                ActivityCompat.requestPermissions(
                    fragment.requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_CODE
                )
            }
        }

    private fun checkForCameraPermissionDeny(): Boolean {
        val check = ActivityCompat.shouldShowRequestPermissionRationale(
            fragment.requireActivity(),
            Manifest.permission.CAMERA
        )
        if (check) {
            fragment.showDialog(
                titleResIdRes = R.string.permission_denied,
                messageResId = R.string.enable_camera_permission_message,
                negativeButtonVisible = true,
                positiveButtonRes = R.string.open_settings,
                positiveAction = { openAppSettings() }
            )
        }
        return check
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri = Uri.fromParts("package", fragment.requireContext().packageName, null)
        intent.data = uri
        fragment.requireActivity().startActivity(intent)
    }

    companion object {
        private const val REQUEST_CAMERA_CODE = 1001
    }
}