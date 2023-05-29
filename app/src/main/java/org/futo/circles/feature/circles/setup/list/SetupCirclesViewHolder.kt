package org.futo.circles.feature.circles.setup.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.R
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.databinding.ListItemSetupCircleBinding
import org.futo.circles.extensions.onClick
import org.futo.circles.model.SetupCircleListItem

class SetupCirclesViewHolder(
    parent: ViewGroup,
    onCircleClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemSetupCircleBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemSetupCircleBinding

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