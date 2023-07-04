package org.futo.circles.core.extensions

import androidx.lifecycle.SavedStateHandle

fun <T> SavedStateHandle.getOrThrow(key: String): T =
    this[key] ?: throw IllegalArgumentException("SavedStateHandle param $key is missing")