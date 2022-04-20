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

    fun isImagePostSelected() = isImagePostLiveData.value ?: false

    fun setImageUri(uri: Uri) {
        selectedImageLiveData.value = uri
    }

    fun getImageUri() = selectedImageLiveData.value
}