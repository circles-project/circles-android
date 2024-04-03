package org.futo.circles.core.model

enum class PollState { Sending, Ready, Voted, Ended }

fun PollState.canEdit() = this == PollState.Sending || this == PollState.Ready

fun PollState.canVote() = this != PollState.Sending && this != PollState.Ended