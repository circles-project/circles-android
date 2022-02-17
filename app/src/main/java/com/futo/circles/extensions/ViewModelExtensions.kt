package com.futo.circles.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private val mainContext: CoroutineContext = Dispatchers.Main
private val ioContext: CoroutineContext = Dispatchers.IO

fun ViewModel.launch(
    dispatcher: CoroutineContext,
    block: suspend CoroutineScope.() -> Unit
): Job = viewModelScope.launch(dispatcher + defaultExceptionHandler()) { this.block() }

fun ViewModel.launchUi(
    block: suspend CoroutineScope.() -> Unit
): Job = viewModelScope.launch(mainContext + defaultExceptionHandler()) { this.block() }

fun ViewModel.launchBg(
    block: suspend CoroutineScope.() -> Unit
): Job = viewModelScope.launch(ioContext + defaultExceptionHandler()) { this.block() }
