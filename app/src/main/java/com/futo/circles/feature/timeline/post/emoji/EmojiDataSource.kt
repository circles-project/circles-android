package com.futo.circles.feature.timeline.post.emoji

import android.content.Context
import android.graphics.Paint
import androidx.core.graphics.PaintCompat
import com.futo.circles.R
import com.futo.circles.model.EmojiCategory
import com.futo.circles.model.EmojiData
import com.futo.circles.model.EmojiItem
import com.google.gson.Gson

class EmojiDataSource(context: Context) {

    fun getCategories() = emojiData.categories

    fun getEmojiesForCategory(categoryId: String): List<EmojiItem> {
        val categoryEmojiKeys =
            emojiData.categories.firstOrNull { it.id == categoryId }?.emojis ?: emptyList()
        return emojiData.emojis.filter { categoryEmojiKeys.contains(it.key) }.values.toList()
    }


    private val paint = Paint()

    private val emojiData = context.resources.openRawResource(R.raw.emoji_picker_datasource)
        .use { input ->
            Gson().fromJson(input.bufferedReader().use { it.readText() }, EmojiData::class.java)
        }
        ?.let { parsedRawData ->
            val withParsedEmojies = parsedRawData.copy(emojis = parseEmojies(parsedRawData))
            withParsedEmojies.copy(
                emojis = filterEmoji(withParsedEmojies),
                categories = filterCategories(withParsedEmojies)
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
            val emojiTitle =
                entry.emojis.getOrNull(0)?.let { parsedRawData.emojis[it]?.emoji } ?: entry.name
            add(EmojiCategory(entry.id, entry.name, emojiTitle, mutableListOf<String>().apply {
                entry.emojis.forEach { e ->
                    parsedRawData.emojis[e]?.let {
                        if (canRenderEmoji(it.emoji)) add(e)
                    }
                }
            }))
        }
    }

    private fun parseEmojies(parsedRawData: EmojiData) = mutableMapOf<String, EmojiItem>().apply {
        parsedRawData.emojis.keys.forEach { key ->
            val origin = parsedRawData.emojis[key] ?: return@forEach
            put(
                key, origin.copy(
                    emoji = fromUnicode(origin.unicode.split("-").joinToString("") { "\\u$it" })
                )
            )
        }
    }

    private fun fromUnicode(unicode: String): String {
        val arr = unicode
            .replace("\\", "")
            .split("u".toRegex())
            .dropLastWhile { it.isEmpty() }
        return buildString {
            for (i in 1 until arr.size) {
                val hexVal = Integer.parseInt(arr[i], 16)
                append(Character.toChars(hexVal))
            }
        }
    }

}