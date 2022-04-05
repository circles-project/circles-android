package com.futo.circles.core.list

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding


interface ViewBindingHolder{

    val baseBinding: ViewBinding get() = viewBinding

    fun inflate(
        parent: ViewGroup,
        inflate: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding
    ): View {
        viewBinding = inflate.invoke(LayoutInflater.from(parent.context), parent, false)
        return viewBinding.root
    }

    private companion object {
        private lateinit var viewBinding: ViewBinding
    }
}

val RecyclerView.ViewHolder.context: Context get() = this.itemView.context

abstract class BaseRvAdapter<T, VH : RecyclerView.ViewHolder>(
    itemCallback: DiffUtil.ItemCallback<T>
) : ListAdapter<T, VH>(itemCallback) {

    companion object {
        @Suppress("FunctionName")
        @SuppressLint("DiffUtilEquals")
        fun <T : IdEntity<*>> DefaultIdEntityCallback() = object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
        }

        @Suppress("FunctionName")
        @SuppressLint("DiffUtilEquals")
        fun <T : IdEntity<*>, C> PayloadIdEntityCallback(
            payload: (old: T, new: T) -> C?
        ) = object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem

            override fun getChangePayload(oldItem: T, newItem: T) = payload(oldItem, newItem)
        }
    }

}