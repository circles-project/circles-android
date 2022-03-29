package com.futo.circles.feature.setup_circles.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.R
import com.futo.circles.core.ViewBindingHolder
import com.futo.circles.databinding.SetupCircleListItemBinding
import com.futo.circles.extensions.onClick
import com.futo.circles.model.SetupCircleListItem

class SetupCirclesViewHolder(
    parent: ViewGroup,
    onCircleClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, SetupCircleListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as SetupCircleListItemBinding

    init {
        onClick(itemView) { position -> onCircleClicked(position) }
    }

    fun bind(data: SetupCircleListItem) {
        with(binding) {
            data.coverUri?.let { ivCircleCover.setImageURI(it) }
                ?: ivCircleCover.setImageResource(R.drawable.add_image_placeholder)

            tvCircleName.text = data.name
            tvUserName.text = data.userName
        }
    }
}