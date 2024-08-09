package org.futo.circles.view.members_list

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.visible
import org.futo.circles.core.model.CirclesUserSummary
import org.futo.circles.databinding.ViewMembersListBinding


class MembersAvatarsListView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewMembersListBinding.inflate(LayoutInflater.from(context), this)

    private val avatarsAdapter = MembersAvatarsListAdapter()

    private val maxAvatarsCount = 6

    init {
        binding.rvMembers.apply {
            adapter = avatarsAdapter
            addItemDecoration(OverlapRecyclerViewDecoration(26))
        }
    }

    fun setData(users: List<CirclesUserSummary>) {
        val listSize = users.size
        if (listSize > maxAvatarsCount) {
            binding.tvPlusCount.apply {
                val plusCountText = "+${listSize - maxAvatarsCount}"
                text = plusCountText
                visible()
            }
            avatarsAdapter.submitList(users.sortedBy { it.id }.subList(0, maxAvatarsCount))

        } else {
            binding.tvPlusCount.gone()
            avatarsAdapter.submitList(users.sortedBy { it.id })
        }
    }

}