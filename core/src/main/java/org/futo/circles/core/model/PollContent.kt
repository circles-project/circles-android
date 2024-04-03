package org.futo.circles.core.model

data class PollContent(
    val question: String,
    val state: PollState,
    val totalVotes: Int,
    val options: List<PollOption>,
    val isClosedType: Boolean
) : PostContent(PostContentType.POLL_CONTENT)