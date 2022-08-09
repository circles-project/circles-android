package org.futo.circles.model

data class EmojiCategory(
    val id: String,
    val name: String,
    val emojiTitle:String,
    val emojis: List<String>
)