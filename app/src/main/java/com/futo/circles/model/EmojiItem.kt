package com.futo.circles.model

import com.futo.circles.core.list.IdEntity
import com.google.gson.annotations.SerializedName

data class EmojiItem(
    @SerializedName("a") val name: String,
    @SerializedName("b") val unicode: String,
    @SerializedName("j") val keywords: List<String> = emptyList(),
    val emoji: String
) : IdEntity<String> {
    override val id: String = name
}