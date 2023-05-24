package org.futo.circles.feature.timeline

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver

class MarkAsReadBuffer(
    private val recyclerView: RecyclerView,
    private val onMarkAsRead: (List<Int>) -> Unit
) {

    private val positionsBuffer = mutableSetOf<Int>()

    init {
        handleItemsInsert()
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                collectPositions()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) callMarkAsRead()
            }
        })
    }

    private fun callMarkAsRead() {
        positionsBuffer.takeIf { it.isNotEmpty() }?.let {
            onMarkAsRead(it.toList())
            positionsBuffer.clear()
        }
    }

    private fun collectPositions() {
        val manager = (recyclerView.layoutManager as? LinearLayoutManager) ?: return
        val firstVisiblePosition = manager.findFirstVisibleItemPosition()
        val lastVisiblePosition = manager.findLastVisibleItemPosition()
        (firstVisiblePosition..lastVisiblePosition).forEach { positionsBuffer.add(it) }
    }

    private fun handleItemsInsert() {
        recyclerView.adapter?.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                recyclerView.post {
                    collectPositions()
                    callMarkAsRead()
                }
            }
        })
    }
}