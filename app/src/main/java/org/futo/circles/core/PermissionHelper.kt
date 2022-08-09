package org.futo.circles.core

import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionHelper(
    private val fragment: Fragment,
    private val onGranted: () -> Unit,
    private val permissionDenied: () -> Unit = {}
) {

    private val requestPermissionLauncher =
        fragment.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                onGranted()
            } else {
                permissionDenied()
            }
        }

    fun checkPermission(permission: String) {
        if (isPermissionGranted(fragment, permission)) {
            onGranted()
        } else {
            requestPermissionLauncher.launch(permission)
        }
    }

    companion object {
        fun isPermissionGranted(
            fragment: Fragment,
            permission: String
        ) = ContextCompat.checkSelfPermission(
            fragment.requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

}