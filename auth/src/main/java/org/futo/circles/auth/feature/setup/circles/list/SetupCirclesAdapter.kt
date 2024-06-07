package org.futo.circles.auth.feature.setup.circles.list

import android.view.ViewGroup
import org.futo.circles.auth.model.SetupCirclesListItem
import org.futo.circles.core.base.list.BaseRvAdapter

class SetupCirclesAdapter(
    private val onChangeImage: (String) -> Unit,
    private val onRemove: (String) -> Unit,
) : BaseRvAdapter<SetupCirclesListItem, SetupCirclesViewHolder>(DefaultIdEntityCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SetupCirclesViewHolder =
        SetupCirclesViewHolder(
            parent = parent,
            onImageClicked = { position ->
                onChangeImage(getItem(position).id)
            },
            onRemoveClicked = { position ->
                onRemove(getItem(position).id)
            }
        )


    override fun onBindViewHolder(holder: SetupCirclesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}