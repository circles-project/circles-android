package org.futo.circles.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.futo.circles.R
import org.futo.circles.databinding.ViewPollOptionBinding
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.model.PollOption

class PollOptionView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewPollOptionBinding.inflate(LayoutInflater.from(context), this)

    fun setup(option: PollOption) {
        with(binding) {
            tvOptionQuestion.text = option.optionAnswer
            ivWinner.setIsVisible(option.isWinner)
            tvVotesCount.text = context.resources.getQuantityString(
                R.plurals.votes, option.voteCount, option.voteCount
            )
            horizontalProgress.progress = option.votePercentage
            if(option.isMyVote){

            }else{

            }
        }

    }

}