package com.futo.circles.feature.post

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreatePostViewModel : ViewModel() {

    val isImagePostLiveData = MutableLiveData(false)


    fun setIsImagePost(isImagePost: Boolean) {
        isImagePostLiveData.postValue(isImagePost)
    }
}