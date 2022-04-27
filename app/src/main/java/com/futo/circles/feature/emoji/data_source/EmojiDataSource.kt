package com.futo.circles.feature.emoji.data_source

import android.content.Context
import android.graphics.Paint
import androidx.core.graphics.PaintCompat
import com.futo.circles.R
import com.futo.circles.model.EmojiCategory
import com.futo.circles.model.EmojiData
import com.futo.circles.model.EmojiItem
import com.google.gson.Gson

class EmojiDataSource(context: Context) {

    private val paint = Paint()

    val emojiData = context.resources.openRawResource(R.raw.emoji_picker_datasource)
        .use { input ->
            Gson().fromJson(input.bufferedReader().use { it.readText() }, EmojiData::class.java)
        }
        ?.let { parsedRawData ->
            parsedRawData.copy(
                emojis = filterEmoji(parsedRawData),
                categories = filterCategories(parsedRawData)
            )
        } ?: EmojiData(emptyList(), emptyMap(), emptyMap())

    private fun canRenderEmoji(emoji: String): Boolean {
        return PaintCompat.hasGlyph(paint, emoji)
    }

    private fun filterEmoji(parsedRawData: EmojiData) = mutableMapOf<String, EmojiItem>().apply {
        parsedRawData.emojis.keys.forEach { key ->
            val origin = parsedRawData.emojis[key] ?: return@forEach
            if (canRenderEmoji(origin.emoji)) {
                if (origin.keywords.contains(key) || key.contains("_")) {
                    put(key, origin)
                } else {
                    put(key, origin.copy(keywords = origin.keywords + key))
                }
            }
        }
    }

    private fun filterCategories(parsedRawData: EmojiData) = mutableListOf<EmojiCategory>().apply {
        parsedRawData.categories.forEach { entry ->
            add(EmojiCategory(entry.id, entry.name, mutableListOf<String>().apply {
                entry.emojis.forEach { e ->
                    parsedRawData.emojis[e]?.let {
                        if (canRenderEmoji(it.emoji)) add(e)
                    }
                }
            }))
        }
    }
}