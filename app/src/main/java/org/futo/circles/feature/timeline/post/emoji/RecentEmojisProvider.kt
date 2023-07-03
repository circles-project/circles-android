package org.futo.circles.feature.timeline.post.emoji

import android.content.Context
import com.vanniktech.emoji.Emoji
import com.vanniktech.emoji.recent.RecentEmojiManager
import com.vanniktech.emoji.search.SearchEmojiManager

object RecentEmojisProvider {

    private var manager: RecentEmojiManager? = null

    fun get(context: Context) = manager ?: RecentEmojiManager(context).also { manager = it }

    fun saveDefaultRecentEmojis(context: Context) {
        val recentManager = get(context)
        val recent = recentManager.getRecentEmojis()
        if (recent.isNotEmpty()) return
        recentManager.apply {
            SearchEmojiManager().apply {
                search("100").firstOrNull()?.emoji?.let { emoji -> addEmoji(emoji) }
                search("tada").firstOrNull()?.emoji?.let { emoji -> addEmoji(emoji) }
                search("grinning").firstOrNull()?.emoji?.let { emoji -> addEmoji(emoji) }
                search("thumbsup").firstOrNull()?.emoji?.let { emoji -> addEmoji(emoji) }
            }
            persist()
        }
    }

    fun addNewEmoji(context: Context, emoji: Emoji) {
        get(context).apply {
            addEmoji(emoji)
            persist()
        }
    }
}