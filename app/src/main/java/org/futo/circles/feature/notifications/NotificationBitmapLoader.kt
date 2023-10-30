package org.futo.circles.feature.notifications

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import androidx.annotation.WorkerThread
import androidx.core.graphics.drawable.IconCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.signature.ObjectKey
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.core.feature.textDrawable.ColorGenerator
import org.futo.circles.core.feature.textDrawable.TextDrawable
import javax.inject.Inject

class NotificationBitmapLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {

    @WorkerThread
    fun getRoomBitmap(roomName: String, path: String?): Bitmap {
        val placeholder = TextDrawable.Builder()
            .setShape(TextDrawable.SHAPE_RECT)
            .setColor(ColorGenerator().getColor(roomName))
            .setTextColor(Color.WHITE)
            .setWidth(64)
            .setHeight(64)
            .setText(roomName.first().toString())
            .build()

        return try {
            Glide.with(context)
                .asBitmap()
                .load(path)
                .format(DecodeFormat.PREFER_ARGB_8888)
                .signature(ObjectKey("room-icon-notification"))
                .error(placeholder)
                .submit()
                .get()
        } catch (e: Exception) {
            placeholder.getBitmap()
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
