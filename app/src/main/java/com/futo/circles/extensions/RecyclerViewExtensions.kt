package com.futo.circles.extensions


import android.view.View
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.ViewHolder.onClick(view: View, perform: (adapterPosition: Int) -> Unit) =
    view.setOnClickListener { bindingAdapterPosition.takeIf { it != -1 }?.let(perform) }