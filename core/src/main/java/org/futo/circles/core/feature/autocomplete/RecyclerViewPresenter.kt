package org.futo.circles.core.feature.autocomplete

import android.content.Context
import android.database.DataSetObserver
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver

/**
 * Simple [AutocompletePresenter] implementation that hosts a [RecyclerView].
 * Supports [android.view.ViewGroup.LayoutParams.WRAP_CONTENT] natively.
 * The only contract is to
 *
 * - provide a [RecyclerView.Adapter] in [.instantiateAdapter]
 * - call [.dispatchClick] when an object is clicked
 * - update your data during [.onQuery]
 *
 * @param <T> your model object (the object displayed by the list)
</T> */
abstract class RecyclerViewPresenter<T>(context: Context) : AutocompletePresenter<T>(context) {
    @get:Suppress("unused")
    private var recyclerView: RecyclerView? = null
        private set
    private var clicks: ClickProvider<T>? = null
    private var observer: Observer? = null
    override fun registerClickProvider(provider: ClickProvider<T>?) {
        clicks = provider
    }

    override fun registerDataSetObserver(observer: DataSetObserver) {
        this.observer = Observer(observer)
    }

    override val view: ViewGroup
        get() {
            recyclerView = RecyclerView(context)
            val adapter = instantiateAdapter()
            recyclerView!!.adapter = adapter
            recyclerView!!.layoutManager = instantiateLayoutManager()
            if (observer != null) {
                adapter.registerAdapterDataObserver(observer!!)
                observer = null
            }
            return recyclerView!!
        }

    override fun onViewShown() {}

    @CallSuper
    override fun onViewHidden() {
        recyclerView = null
        observer = null
    }

    /**
     * Dispatch click event to [AutocompleteCallback].
     * Should be called when items are clicked.
     *
     * @param item the clicked item.
     */
    protected fun dispatchClick(item: T) {
        if (clicks != null) clicks!!.click(item)
    }

    /**
     * Request that the popup should recompute its dimensions based on a recent change in
     * the view being displayed.
     *
     * This is already managed internally for [RecyclerView] events.
     * Only use it for changes in other views that you have added to the popup,
     * and only if one of the dimensions for the popup is WRAP_CONTENT .
     */
    @Suppress("unused")
    protected fun dispatchLayoutChange() {
        observer?.onChanged()
    }

    /**
     * Provide an adapter for the recycler.
     * This should be a fresh instance every time this is called.
     *
     * @return a new adapter.
     */
    protected abstract fun instantiateAdapter(): RecyclerView.Adapter<*>

    /**
     * Provides a layout manager for the recycler.
     * This should be a fresh instance every time this is called.
     * Defaults to a vertical LinearLayoutManager, which is guaranteed to work well.
     *
     * @return a new layout manager.
     */
    protected fun instantiateLayoutManager(): RecyclerView.LayoutManager {
        return LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private class Observer(private val root: DataSetObserver) :
        AdapterDataObserver() {
        override fun onChanged() {
            root.onChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            root.onChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            root.onChanged()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            root.onChanged()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            root.onChanged()
        }
    }
}