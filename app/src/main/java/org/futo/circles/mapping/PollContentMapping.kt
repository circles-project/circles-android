package org.futo.circles.mapping

import org.futo.circles.model.PollContent
import org.futo.circles.model.PollOption
import org.futo.circles.model.PollState
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessagePollContent
import org.matrix.android.sdk.api.session.room.model.message.PollAnswer
import org.matrix.android.sdk.api.session.room.model.message.PollType
import org.matrix.android.sdk.api.session.room.send.SendState
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

fun TimelineEvent.toPollContent(): PollContent {
    val pollContent = root.getClearContent().toModel<MessagePollContent>()
    val pollCreationInfo = pollContent?.getBestPollCreationInfo()
    val pollResponseData = this.annotations?.pollResponseSummary?.let {
        PollResponseData(
            myVote = it.aggregatedContent?.myVote,
            isClosed = it.closedTime != null,
            votes = it.aggregatedContent?.votesSummary?.mapValues { votesSummary ->
                PollVoteSummaryData(
                    total = votesSummary.value.total,
                    percentage = votesSummary.value.percentage
                )
            },
            winnerVoteCount = it.aggregatedContent?.winnerVoteCount ?: 0,
            totalVotes = it.aggregatedContent?.totalVotes ?: 0
        )
    }

    val pollState = getPollState(
        this.root.sendState,
        pollResponseData
    )
    return PollContent(
        question = pollCreationInfo?.question?.getBestQuestion().orEmpty(),
        state = pollState,
        totalVotes = pollResponseData?.totalVotes ?: 0,
        options = pollCreationInfo?.answers?.toPollOptions(pollResponseData, pollState)
            ?: emptyList(),
        isClosedType = pollCreationInfo?.kind == PollType.UNDISCLOSED
    )
}

private data class PollResponseData(
    val myVote: String?,
    val votes: Map<String, PollVoteSummaryData>?,
    val totalVotes: Int,
    val winnerVoteCount: Int,
    val isClosed: Boolean
)

private data class PollVoteSummaryData(
    val total: Int,
    val percentage: Double
)

private fun getPollState(
    sendState: SendState,
    pollResponseData: PollResponseData?
) = when {
    !sendState.isSent() -> PollState.Sending
    pollResponseData?.isClosed.orFalse() -> PollState.Ended
    pollResponseData?.myVote?.isNotEmpty().orFalse() -> PollState.Voted
    else -> PollState.Ready
}

private fun List<PollAnswer>.toPollOptions(
    pollResponseData: PollResponseData?,
    pollState: PollState
): List<PollOption> =
    map {
        val vote = pollResponseData?.votes?.get(it.id)
        val voteCount = vote?.total ?: 0
        val winnerVoteCount = pollResponseData?.winnerVoteCount ?: 0
        PollOption(
            optionId = it.id ?: "",
            optionAnswer = it.getBestAnswer() ?: "",
            voteCount = voteCount,
            voteProgress = ((vote?.percentage ?: 0.0) * 100).toInt(),
            isMyVote = pollResponseData?.myVote == it.id,
            isWinner = winnerVoteCount != 0 && voteCount == winnerVoteCount && pollState == PollState.Ended
        )
    }