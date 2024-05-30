package org.futo.circles.core.feature.notifications

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.WorkerThread
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.graphics.drawable.IconCompat
import com.bumptech.glide.Glide
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.core.extensions.dpToPx
import org.futo.circles.core.glide.GlideShortcutUtils.adaptiveShortcutDrawable
import org.futo.circles.core.glide.GlideShortcutUtils.shortcutDrawable
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.util.toMatrixItem
import javax.inject.Inject

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
private val useAdaptiveIcon = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
private const val adaptiveIconSizeDp = 108
private const val adaptiveIconOuterSidesDp = 18

class ShortcutCreator @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val adaptiveIconSize = context.dpToPx(adaptiveIconSizeDp)
    private val adaptiveIconOuterSides = context.dpToPx(adaptiveIconOuterSidesDp)
    private val iconSize by lazy {
        if (useAdaptiveIcon) {
            adaptiveIconSize - (adaptiveIconOuterSides * 2)
        } else {
            context.dpToPx(72)
        }
    }

    @WorkerThread
    fun create(roomSummary: RoomSummary, rank: Int = 1): ShortcutInfoCompat {
        val intent = getOpenRoomIntent(context, roomSummary.roomId)
        val bitmap = try {
            val glideRequests = Glide.with(context)
            val matrixItem = roomSummary.toMatrixItem()
            when (useAdaptiveIcon) {
                true -> adaptiveShortcutDrawable(
                    glideRequests,
                    matrixItem,
                    iconSize,
                    adaptiveIconSize,
                    adaptiveIconOuterSides.toFloat()
                )

                false -> shortcutDrawable(glideRequests, matrixItem, iconSize)
            }
        } catch (failure: Throwable) {
            null
        }
        val categories = mutableSetOf<String>()
        if (Build.VERSION.SDK_INT >= 25) {
            categories.add(ShortcutInfo.SHORTCUT_CATEGORY_CONVERSATION)
        }

        return ShortcutInfoCompat.Builder(context, roomSummary.roomId)
            .setShortLabel(roomSummary.displayName)
            .setIcon(bitmap?.toProfileImageIcon())
            .setLongLived(true)
            .setRank(rank)
            .setCategories(categories).apply {
                intent?.let { setIntent(it) }
            }
            .build()
    }

    private fun Bitmap.toProfileImageIcon(): IconCompat =
        if (useAdaptiveIcon) IconCompat.createWithAdaptiveBitmap(this)
        else IconCompat.createWithBitmap(this)

    private fun getOpenRoomIntent(context: Context, roomId: String): Intent? =
        context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            action = "OPEN_ROOM"
            putExtra("roomId", roomId)
        }

}