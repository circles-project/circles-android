package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import org.futo.circles.R
import org.futo.circles.databinding.ViewPollOptionBinding
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.model.PollOption
import org.futo.circles.model.PollState

class PollOptionView(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    private val binding =
        ViewPollOptionBinding.inflate(LayoutInflater.from(context), this)

    fun setup(option: PollOption, pollState: PollState) {
        with(binding) {
            tvOptionQuestion.text = option.optionAnswer
            ivWinner.setIsVisible(option.isWinner)
        }
        setVotesProgress(pollState, option.voteCount, option.voteProgress)
        setOptionBackground(pollState, option.isWinner, option.isMyVote)
        setCheckIcon(pollState, option.isMyVote)
    }

    private fun setVotesProgress(pollState: PollState, voteCount: Int, voteProgress: Int) {
        val isVisible = pollState == PollState.Voted || pollState == PollState.Ended
        binding.tvVotesCount.setIsVisible(isVisible)
        binding.tvVotesCount.text = context.resources.getQuantityString(
            R.plurals.votes, voteCount, voteCount
        )
        binding.horizontalProgress.progress = if (isVisible) voteProgress else 0
    }

    private fun setCheckIcon(pollState: PollState, isMyVote: Boolean) {
        binding.ivCheck.setIsVisible(pollState != PollState.Ended)
        binding.ivCheck.setImageResource(
            if (isMyVote) R.drawable.ic_check_circle else R.drawable.ic_unselected
        )
    }

    private fun setOptionBackground(pollState: PollState, isWinner: Boolean, isMyVote: Boolean) {
        binding.root.setBackgroundResource(
            if (pollState == PollState.Ended) {
                if (isWinner) R.drawable.bg_border_selected else R.drawable.bg_border
            } else {
                if (isMyVote) R.drawable.bg_border_selected else R.drawable.bg_border
            }
        )
    }

}