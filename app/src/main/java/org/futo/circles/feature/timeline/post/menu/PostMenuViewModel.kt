package org.futo.circles.feature.timeline.post.menu

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.extensions.getCurrentUserPowerLevel
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.feature.timeline.post.PostContentDataSource
import org.futo.circles.core.model.PollContent
import org.futo.circles.core.model.PollState
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.model.canEdit
import org.matrix.android.sdk.api.session.room.powerlevels.Role
import javax.inject.Inject


@HiltViewModel
class PostMenuViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postContentDataSource: PostContentDataSource
) : ViewModel() {

    val roomId: String = savedStateHandle.getOrThrow("roomId")
    val eventId: String = savedStateHandle.getOrThrow("eventId")

    private val userPowerLevel = getCurrentUserPowerLevel(roomId)

    fun getPostContent() = postContentDataSource.getPostContent(roomId, eventId)

    fun getSenderId() = postContentDataSource.getPost(roomId, eventId)?.postInfo?.sender?.userId

    fun canDeletePost() = if (getPostContent()?.type == PostContentType.OTHER_CONTENT) false
    else (areUserAbleToPost() && isMyPost()) || userPowerLevel >= Role.Moderator.value

    fun canEditPost() = if (getPostContent()?.type == PostContentType.OTHER_CONTENT) false
    else areUserAbleToPost() && isMyPost()
            && getPostContent()?.type != PostContentType.POLL_CONTENT

    fun canEndPoll() = isPoll() && getPollState() != PollState.Ended && canDeletePost()

    fun canEditPoll() =
        isPoll() && isMyPost() && areUserAbleToPost() && getPollState()?.canEdit() == true

    fun isMediaPost(): Boolean = getPostContent()?.isMedia() == true

    fun isMyPost(): Boolean = postContentDataSource.getPost(roomId, eventId)?.isMyPost() == true

    private fun areUserAbleToPost() = userPowerLevel >= Role.Default.value

    private fun isPoll(): Boolean = getPostContent()?.isPoll() == true

    private fun getPollState() = (getPostContent() as? PollContent)?.state

}
