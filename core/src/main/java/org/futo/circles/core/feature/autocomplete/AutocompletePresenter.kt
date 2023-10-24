package org.futo.circles.core.feature.autocomplete

import android.content.Context
import android.database.DataSetObserver
import android.view.ViewGroup

/**
 * Base class for presenting items inside a popup. This is abstract and must be implemented.
 *
 * Most important methods are [.getView] and [.onQuery].
 */
abstract class AutocompletePresenter<T>(
    /**
     * @return this presenter context
     */
    protected val context: Context
) {

    /**
     * @return whether we are showing currently
     */
    @get:Suppress("unused")
    private var isShowing = false

    /**
     * At this point the presenter is passed the [ClickProvider].
     * The contract is that [ClickProvider.click] must be called when a list item
     * is clicked. This ensure that the autocomplete callback will receive the event.
     *
     * @param provider a click provider for this presenter.
     */
    open fun registerClickProvider(provider: ClickProvider<T>?) {}

    /**
     * Useful if you wish to change width/height based on content height.
     * The contract is to call [DataSetObserver.onChanged] when your view has
     * changes.
     *
     * This is called after [.getView].
     *
     * @param observer the observer.
     */
    open fun registerDataSetObserver(observer: DataSetObserver) {}
    abstract val view: ViewGroup
    val popupDimensions: PopupDimensions
        /**
         * Provide the [PopupDimensions] for this popup. Called just once.
         * You can use fixed dimensions or [android.view.ViewGroup.LayoutParams.WRAP_CONTENT] and
         * [android.view.ViewGroup.LayoutParams.MATCH_PARENT].
         *
         * @return a PopupDimensions object
         */
        get() = PopupDimensions()

    /**
     * Perform firther initialization here. Called after [.getView],
     * each time the popup is shown.
     */
    protected abstract fun onViewShown()

    /**
     * Called to update the view to filter results with the query.
     * It is called any time the popup is shown, and any time the text changes and query is updated.
     *
     * @param query query from the edit text, to filter our results
     */
    abstract fun onQuery(query: CharSequence?)

    /**
     * Called when the popup is hidden, to release resources.
     */
    protected abstract fun onViewHidden()
    fun showView() {
        isShowing = true
        onViewShown()
    }

    fun hideView() {
        isShowing = false
        onViewHidden()
    }

    interface ClickProvider<T> {
        fun click(item: T)
    }

    /**
     * Provides width, height, maxWidth and maxHeight for the popup.
     * @see .getPopupDimensions
     */
    class PopupDimensions {
        var width = ViewGroup.LayoutParams.WRAP_CONTENT
        var height = ViewGroup.LayoutParams.WRAP_CONTENT
        var maxWidth = Int.MAX_VALUE
        var maxHeight = Int.MAX_VALUE
    }
}