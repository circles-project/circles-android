package org.futo.circles.settings.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import org.futo.circles.core.extensions.dpToPx
import org.futo.circles.settings.databinding.ViewPeopleCategoriesBinding
import org.futo.circles.settings.model.PeopleCategoryData
import org.futo.circles.settings.model.PeopleCategoryType
import org.futo.circles.settings.model.PeopleCategoryType.Followers
import org.futo.circles.settings.model.PeopleCategoryType.Following
import org.futo.circles.settings.model.PeopleCategoryType.Other

class PeopleCategoriesView(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding = ViewPeopleCategoriesBinding.inflate(LayoutInflater.from(context), this)

    private var selectCategoryListener: ((PeopleCategoryType) -> Unit)? = null

    init {
        with(binding) {
            cvFollowers.setOnClickListener { setSelected(Followers) }
            cvFollowing.setOnClickListener { setSelected(Following) }
            cvSuggestions.setOnClickListener { setSelected(Other) }
        }
    }

    fun setOnCategorySelectListener(listener: (PeopleCategoryType) -> Unit) {
        selectCategoryListener = listener
    }

    fun setCountsPerCategory(categoryMap: Map<PeopleCategoryType, PeopleCategoryData>) {
        categoryMap.keys.forEach {
            val count = categoryMap[it]?.count ?: 0
            when (it) {
                Followers -> setFollowersCount(count)
                Following -> setFollowingCount(count)
                Other -> setSuggestionsCount(count)
            }
        }
    }

    private fun setSelected(type: PeopleCategoryType) {
        selectCategoryListener?.invoke(type)
        with(binding) {
            cvFollowers.strokeWidth = 0
            cvFollowing.strokeWidth = 0
            cvSuggestions.strokeWidth = 0
            val borderWidth = context.dpToPx(1)
            when (type) {
                Followers -> cvFollowers.strokeWidth = borderWidth
                Following -> cvFollowing.strokeWidth = borderWidth
                Other -> cvSuggestions.strokeWidth = borderWidth
            }
        }
    }

    private fun setFollowersCount(count: Int) {
        binding.tvFollowersCount.text = count.toString()
    }

    private fun setFollowingCount(count: Int) {
        binding.tvFollowingCount.text = count.toString()
    }

    private fun setSuggestionsCount(count: Int) {
        binding.tvSuggestionsCount.text = count.toString()
    }

}