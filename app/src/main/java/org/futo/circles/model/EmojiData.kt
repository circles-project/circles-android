package org.futo.circles.model

data class EmojiData(
    val categories: List<EmojiCategory>,
    val emojis: Map<String, EmojiItem>,
    val aliases: Map<String, String>
)