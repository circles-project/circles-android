package org.futo.circles.core.model

data class PollResponseData(
    val myVote: String?,
    val votes: Map<String, PollVoteSummaryData>?,
    val totalVotes: Int,
    val winnerVoteCount: Int,
    val isClosed: Boolean
)