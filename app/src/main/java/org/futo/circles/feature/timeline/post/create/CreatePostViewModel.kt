package org.futo.circles.feature.timeline.post.create

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.model.PostContent
import org.futo.circles.core.timeline.post.PostContentDataSource
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postContentDataSource: PostContentDataSource
) : ViewModel() {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")
    private val eventId: String? = savedStateHandle["eventId"]
    private val isEdit: Boolean = savedStateHandle.getOrThrow("isEdit")

    val postToEditContentLiveData = MutableLiveData<PostContent>()

    init {
        if (isEdit) setEditPostInfo()
    }

    private fun setEditPostInfo() {
        eventId ?: return
        val content = postContentDataSource.getPostContent(roomId, eventId) ?: return
        postToEditContentLiveData.value = content
    }
}