package org.futo.circles.feature.notifications

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.WorkerThread
import androidx.core.graphics.drawable.IconCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.signature.ObjectKey

//TODO Singleton
class NotificationBitmapLoader(private val context: Context) {

    @WorkerThread
    fun getRoomBitmap(path: String?): Bitmap? {
        if (path == null) {
            return null
        }
        return loadRoomBitmap(path)
    }

    @WorkerThread
    private fun loadRoomBitmap(path: String): Bitmap? {
        return try {
            Glide.with(context)
                .asBitmap()
                .load(path)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .signature(ObjectKey("room-icon-notification"))
                .submit()
                .get()
        } catch (e: Exception) {
            null
        }
    }

    @WorkerThread
    fun getUserIcon(path: String?): IconCompat? {
        if (path == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return null
        }

        return loadUserIcon(path)
    }

    @WorkerThread
    private fun loadUserIcon(path: String): IconCompat? {
        return try {
            val bitmap = Glide.with(context)
                .asBitmap()
                .load(path)
                .transform(CircleCrop())
                .format(DecodeFormat.PREFER_ARGB_8888)
                .signature(ObjectKey("user-icon-notification"))
                .submit()
                .get()
            IconCompat.createWithBitmap(bitmap)
        } catch (e: Exception) {
            null
        }
    }
}
