package org.futo.circles.feature.rageshake

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import org.futo.circles.BuildConfig
import org.futo.circles.R
import org.futo.circles.extensions.getAllChildFragments
import org.futo.circles.provider.MatrixInstanceProvider
import org.futo.circles.provider.MatrixSessionProvider
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.zip.GZIPOutputStream

class BugReportDataCollector(private val context: Context) {

    var screenshot: Bitmap? = null
        private set

    fun collectData(
        description: String,
        contactInfo: String,
        sendLogs: Boolean,
        sendScreenshot: Boolean
    ): Map<String, Any> {
        val session = MatrixSessionProvider.currentSession
        val map = mutableMapOf<String, Any>(
            "app" to context.getString(R.string.app_name),
            "label" to context.getString(R.string.rage_shake_report),
            "user_agent" to MatrixInstanceProvider.matrix.getUserAgent(),
            "platform" to "Android",
            "version" to BuildConfig.VERSION_NAME,
            "flavour" to BuildConfig.FLAVOR,
            "build_type" to BuildConfig.BUILD_TYPE,
            "user_id" to (session?.myUserId ?: ""),
            "device_id" to (session?.sessionParams?.deviceId ?: ""),
            "home_server_url" to (session?.sessionParams?.homeServerUrl ?: ""),
            "text" to description,
            "email" to contactInfo,
            "device" to Build.MODEL.trim(),
            "os" to Build.VERSION.RELEASE + " (API " + Build.VERSION.SDK_INT + ") " +
                    Build.VERSION.INCREMENTAL + "-" + Build.VERSION.CODENAME

        )
        if (sendLogs) saveLogCat()?.let { map["compressed-log"] = it }
        if (sendScreenshot) saveScreenshot()?.let { map["file"] = it }
        return map
    }

    private fun saveLogCat(): File? {
        val logCatErrFile = File(context.cacheDir.absolutePath, LOG_CAT_FILENAME)
        if (logCatErrFile.exists()) logCatErrFile.delete()
        try {
            logCatErrFile.writer().use { getLogCatError(it) }
            return compressFile(logCatErrFile)
        } catch (_: Throwable) {
        }
        return null
    }

    private fun getLogCatError(streamWriter: OutputStreamWriter) {
        val logcatProcess: Process
        try {
            logcatProcess = Runtime.getRuntime().exec(LOGCAT_CMD_DEBUG)
        } catch (_: IOException) {
            return
        }
        try {
            val separator = System.getProperty("line.separator")
            logcatProcess.inputStream
                .reader()
                .buffered(BUFFER_SIZE)
                .forEachLine { line ->
                    streamWriter.append(line)
                    streamWriter.append(separator)
                }
        } catch (_: IOException) {
        }
    }


    private fun compressFile(fin: File): File? {
        val dstFile = fin.resolveSibling(fin.name + ".gz")
        if (dstFile.exists()) dstFile.delete()
        try {
            GZIPOutputStream(dstFile.outputStream()).use { gos ->
                fin.inputStream().use { it.copyTo(gos, 2048) }
            }
            return dstFile
        } catch (_: Throwable) {
        }
        return null
    }

    private fun saveScreenshot(): File? {
        val bitmap = screenshot ?: return null
        val screenshotFile =
            File(context.cacheDir.absolutePath, LOG_CAT_SCREENSHOT_FILENAME)
        if (screenshotFile.exists()) screenshotFile.delete()
        try {
            screenshotFile.outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        } catch (_: Exception) {
            return null
        }
        return screenshotFile
    }

    fun takeScreenshot(activity: FragmentActivity) {
        screenshot = null
        val rootView = activity.window?.decorView?.rootView ?: return
        val mainBitmap = getBitmap(rootView) ?: return
        return try {
            val bitmap =
                Bitmap.createBitmap(mainBitmap.width, mainBitmap.height, Bitmap.Config.ARGB_8888)
            Canvas(bitmap).apply {
                drawBitmap(mainBitmap, 0f, 0f, null)
                getDialogBitmaps(activity).forEach { drawBitmap(it, 0f, 0f, null) }
            }
            screenshot = bitmap
        } catch (_: Throwable) {
        }
    }

    private fun getDialogBitmaps(activity: FragmentActivity): List<Bitmap> {
        return activity.supportFragmentManager.fragments
            .map { it.getAllChildFragments() }
            .flatten()
            .filterIsInstance(DialogFragment::class.java)
            .mapNotNull { fragment ->
                fragment.dialog?.window?.decorView?.rootView?.let { rootView -> getBitmap(rootView) }
            }
    }

    @Suppress("DEPRECATION")
    private fun getBitmap(rootView: View): Bitmap? {
        rootView.isDrawingCacheEnabled = false
        rootView.isDrawingCacheEnabled = true
        return try {
            rootView.drawingCache
        } catch (e: Throwable) {
            null
        }
    }

    companion object {
        private const val LOG_CAT_FILENAME = "logcat.log"
        private const val LOG_CAT_SCREENSHOT_FILENAME = "screenshot.png"
        private const val BUFFER_SIZE = 1024 * 1024 * 50
        private val LOGCAT_CMD_DEBUG = arrayOf("logcat", "-d", "-v", "threadtime", "*:*")
    }
}