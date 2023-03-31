package org.futo.circles.feature.rageshake

import android.app.Activity
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.squareup.seismic.ShakeDetector
import org.futo.circles.R

class RageShake(private val activity: AppCompatActivity) : ShakeDetector.Listener {

    private var shakeDetector: ShakeDetector? = null
    private var dialogDisplayed = false


    fun start() {
        val sensorManager = activity.getSystemService<SensorManager>() ?: return
        shakeDetector = ShakeDetector(this).apply {
            start(sensorManager, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        shakeDetector?.stop()
    }

    override fun hearShake() {
        if (dialogDisplayed) return
        dialogDisplayed = true
        vibrate()
        MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.shake_detected)
            .setMessage(R.string.shake_detected_message)
            .setPositiveButton(R.string.yes) { _, _ -> openBugReportScreen() }
            .setOnDismissListener { dialogDisplayed = false }
            .setNegativeButton(R.string.no, null)
            .show()

    }

    private fun openBugReportScreen() {
        BugReportDialogFragment.show(activity)
    }

    private fun vibrate() {
        val vibrator = activity.getSystemService<Vibrator>() ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(100L, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(100L)
        }
    }
}
