package com.futo.circles.feature.post

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreatePostViewModel : ViewModel() {

    val isImagePostLiveData = MutableLiveData(false)
    val selectedImageLiveData = MutableLiveData<Uri>()

    fun setIsImagePost(isImagePost: Boolean) {
        isImagePostLiveData.postValue(isImagePost)
    }

    fun setImageUri(uri: Uri) {
        selectedImageLiveData.value = uri
    }
}