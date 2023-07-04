package org.futo.circles.core.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

private val mainContext: CoroutineContext = Dispatchers.Main
private val ioContext: CoroutineContext = Dispatchers.IO

fun ViewModel.launchUi(
    block: suspend CoroutineScope.() -> Unit
): Job = viewModelScope.launch(mainContext + defaultExceptionHandler()) { this.block() }

fun ViewModel.launchBg(
    exceptionHandler: CoroutineExceptionHandler = defaultExceptionHandler(),
    block: suspend CoroutineScope.() -> Unit
): Job = viewModelScope.launch(ioContext + exceptionHandler) { this.block() }
