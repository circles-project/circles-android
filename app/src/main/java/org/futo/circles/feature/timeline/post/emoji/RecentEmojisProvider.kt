package org.futo.circles.feature.timeline.post.emoji

import android.content.Context
import com.vanniktech.emoji.Emoji
import com.vanniktech.emoji.recent.RecentEmojiManager

object RecentEmojisProvider {

    private var manager: RecentEmojiManager? = null

    private val defaultEmojisKeys = listOf(
        "👍", "👎", "❤️", "🔥", "😀", "🤣", "🎉", "🚀", "💯"
    )

    private const val RECENT_EMOJIS_PREF_NAME = "emoji-recent-manager"
    private const val RECENT_EMOJIS_PREF_KEY = "recent-emojis"
    private const val TIME_DELIMITER = ";"
    private const val EMOJI_DELIMITER = "~"

    fun get(context: Context) = manager ?: RecentEmojiManager(context).also { manager = it }

    fun initWithDefaultRecentEmojis(context: Context) {
        val defaultEmojisString =
            defaultEmojisKeys.joinToString(TIME_DELIMITER + "0" + EMOJI_DELIMITER)
        context.applicationContext.getSharedPreferences(
            RECENT_EMOJIS_PREF_NAME, Context.MODE_PRIVATE
        ).edit().putString(RECENT_EMOJIS_PREF_KEY, defaultEmojisString).apply()
        manager = RecentEmojiManager(context)
    }

    fun addNewEmoji(context: Context, emoji: Emoji) {
        get(context).apply {
            addEmoji(emoji)
            persist()
        }
    }
}