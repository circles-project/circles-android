package org.futo.circles.core.extensions


import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

fun RecyclerView.ViewHolder.onClick(view: View, perform: (adapterPosition: Int) -> Unit) =
    view.setOnClickListener { bindingAdapterPosition.takeIf { it != -1 }?.let(perform) }

fun RecyclerView.bindToFab(fab: FloatingActionButton) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (dy > 0 && fab.isVisible) fab.hide()
            else if (dy < 0 && fab.visibility != View.VISIBLE) fab.show()
        }
    })
}