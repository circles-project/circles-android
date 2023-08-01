package org.futo.circles.core.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.futo.circles.core.databinding.ViewLoadingRecyclerBinding
import org.futo.circles.core.extensions.bindToFab
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.visible
import org.matrix.android.sdk.api.extensions.tryOrNull

class LoadingRecyclerView(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding = ViewLoadingRecyclerBinding.inflate(LayoutInflater.from(context), this)

    private val dataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(positionStart, itemCount)
            notifyItemsChanged()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            super.onItemRangeRemoved(positionStart, itemCount)
            notifyItemsChanged()
        }
    }

    var adapter: RecyclerView.Adapter<*>? = null
        get() = binding.rvList.adapter
        set(value) {
            binding.rvList.adapter = value
            field = value
            setupDataObserver()
        }

    var layoutManager: RecyclerView.LayoutManager? = null
        get() = binding.rvList.layoutManager
        set(value) {
            binding.rvList.layoutManager = value
            field = value
        }


    fun setEmptyView(view: View) {
        binding.lEmptyViewContainer.addView(view)
    }

    fun addItemDecoration(decoration: ItemDecoration) {
        binding.rvList.addItemDecoration(decoration)
    }

    fun getRecyclerView() = binding.rvList

    fun bindToFab(fab: FloatingActionButton) = binding.rvList.bindToFab(fab)

    private fun setupDataObserver() {
        with(binding) {
            val initialCount = rvList.adapter?.itemCount ?: 0
            if (initialCount == 0) {
                vLoading.visible()
                rvList.adapter?.registerAdapterDataObserver(dataObserver)
            } else {
                tryOrNull { rvList.adapter?.unregisterAdapterDataObserver(dataObserver) }
                vLoading.gone()
            }
        }
    }

    fun notifyItemsChanged() {
        binding.vLoading.gone()
        val itemsCount = binding.rvList.adapter?.itemCount ?: 0
        binding.lEmptyViewContainer.setIsVisible(itemsCount == 0)
    }
}