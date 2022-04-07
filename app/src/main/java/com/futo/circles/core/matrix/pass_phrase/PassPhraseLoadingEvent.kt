package com.futo.circles.core.matrix.pass_phrase

import androidx.annotation.StringRes

sealed class PassPhraseLoadingEvent

object StartPassPhraseLoading : PassPhraseLoadingEvent()
object FinisPassPhraseLoading : PassPhraseLoadingEvent()
data class LoadingPassPhrase(@StringRes val messageId: Int) : PassPhraseLoadingEvent()