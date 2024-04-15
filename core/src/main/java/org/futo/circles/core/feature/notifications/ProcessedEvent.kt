package org.futo.circles.core.feature.notifications

data class ProcessedEvent<T>(
        val type: Type,
        val event: T
) {

    enum class Type {
        KEEP,
        REMOVE
    }
}

fun <T> List<ProcessedEvent<T>>.onlyKeptEvents() = mapNotNull { processedEvent ->
    processedEvent.event.takeIf { processedEvent.type == ProcessedEvent.Type.KEEP }
}
