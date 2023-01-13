package org.futo.circles.feature.notifications


enum class BackgroundSyncMode {

    FDROID_BACKGROUND_SYNC_MODE_FOR_BATTERY,

    FDROID_BACKGROUND_SYNC_MODE_FOR_REALTIME,

    FDROID_BACKGROUND_SYNC_MODE_DISABLED;

    companion object {
        const val DEFAULT_SYNC_DELAY_SECONDS = 60
        const val DEFAULT_SYNC_TIMEOUT_SECONDS = 6

        fun fromString(value: String?): BackgroundSyncMode =
            values().firstOrNull { it.name == value }
                ?: FDROID_BACKGROUND_SYNC_MODE_DISABLED
    }
}
