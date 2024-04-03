package org.futo.circles.core.model

data class PollOption(
    val optionId: String,
    val optionAnswer: String,
    val voteCount: Int,
    val voteProgress: Int,
    val isMyVote: Boolean,
    val isWinner: Boolean
)