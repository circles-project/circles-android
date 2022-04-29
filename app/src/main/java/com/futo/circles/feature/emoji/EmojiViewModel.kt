package com.futo.circles.feature.emoji

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.feature.emoji.data_source.EmojiDataSource
import com.futo.circles.model.EmojiItem

class EmojiViewModel(private val emojiDataSource: EmojiDataSource) : ViewModel() {

    val categoriesLiveData = MutableLiveData(emojiDataSource.getCategories())
    val emojiesForCategoryLiveData = MutableLiveData<List<EmojiItem>>()

    fun onEmojiTabSelected(categoryId: String) {
        val emojiesForCategory = emojiDataSource.getEmojiesForCategory(categoryId)
        emojiesForCategoryLiveData.postValue(emojiesForCategory)
    }
}