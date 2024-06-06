package org.futo.circles.auth.feature.setup.circles.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.auth.databinding.ListItemSetupCirclesBinding
import org.futo.circles.auth.model.SetupCirclesListItem
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.provider.MatrixSessionProvider

class SetupCirclesViewHolder(
    parent: ViewGroup,
    onImageClicked: (Int) -> Unit,
    onRemoveClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemSetupCirclesBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemSetupCirclesBinding

    init {
        onClick(binding.ivCircle) { position -> onImageClicked(position) }
        onClick(binding.ivRemove) { position -> onRemoveClicked(position) }
    }

    fun bind(data: SetupCirclesListItem) {
        with(binding) {
            ivCircle.setImageURI(data.uri)
            tvCircleTitle.text = data.name
            tvUserId.text = MatrixSessionProvider.currentSession?.myUserId ?: ""
        }
    }
}