package com.futo.circles.feature.emoji

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.feature.emoji.data_source.EmojiDataSource

class EmojiViewModel(private val emojiDataSource: EmojiDataSource) : ViewModel() {

    val categoriesLiveData = MutableLiveData(emojiDataSource.emojiData.categories)
}