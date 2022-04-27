package com.futo.circles.model

import com.google.gson.annotations.SerializedName

data class EmojiItem(
    @SerializedName("a") val name: String,
    @SerializedName("b") val unicode: String,
    @SerializedName("j") val keywords: List<String> = emptyList()
) {

    var cache: String? = null

    val emoji: String
        get() {
            cache?.let { return it }

            val utf8Text = unicode
                .split("-")
                .joinToString("") { "\\u$it" }
            return fromUnicode(utf8Text)
                .also { cache = it }
        }

    companion object {
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
}