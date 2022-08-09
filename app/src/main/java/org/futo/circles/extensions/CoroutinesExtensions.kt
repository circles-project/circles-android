package org.futo.circles.extensions

import org.futo.circles.extensions.DispatcherHolder.BG
import org.futo.circles.extensions.DispatcherHolder.UI
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Hold various implementations of [Dispatchers] to launch coroutines in main and background
 */
object DispatcherHolder {

    val UI = Dispatchers.Main

    val BG = Dispatchers.IO
}

/**
 * Default implementation of [CoroutineExceptionHandler] to print stack stace
 */
fun defaultExceptionHandler() = CoroutineExceptionHandler { _, throwable ->
    throwable.printStackTrace()
}

/**
 * Invoke block in UI context
 */
suspend fun <T> onUI(block: suspend CoroutineScope.() -> T) = withContext(UI, block)

/**
 * Invoke block in BG context
 */
suspend fun <T> onBG(block: suspend CoroutineScope.() -> T) = withContext(BG, block)
