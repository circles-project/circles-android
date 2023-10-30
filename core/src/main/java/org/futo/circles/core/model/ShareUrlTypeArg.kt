package org.futo.circles.core.model

enum class ShareUrlTypeArg(val typeKey: String) {
    ROOM("room"),
    PROFILE("profile"),
    GALLERY("galley"),
    GROUP("group"),
    TIMELINE("timeline")
}

fun shareUrlTypeArgFromType(type: String): ShareUrlTypeArg? {
    val urlType: ShareUrlTypeArg? = null
    ShareUrlTypeArg.values().forEach { if (type == it.typeKey) return it }
    return urlType
}