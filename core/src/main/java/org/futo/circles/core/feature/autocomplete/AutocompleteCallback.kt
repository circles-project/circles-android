package org.futo.circles.core.feature.autocomplete

import android.text.Editable

/**
 * Optional callback to be passed to [Autocomplete.Builder].
 */
interface AutocompleteCallback<T> {
    /**
     * Called when an item inside your list is clicked.
     * This works if your presenter has dispatched a click event.
     * At this point you can edit the text, e.g. `editable.append(item.toString())`.
     *
     * @param editable editable text that you can work on
     * @param item item that was clicked
     * @return true if the action is valid and the popup can be dismissed
     */
    fun onPopupItemClicked(editable: Editable, item: T): Boolean

    /**
     * Called when popup visibility state changes.
     *
     * @param shown true if the popup was just shown, false if it was just hidden
     */
    fun onPopupVisibilityChanged(shown: Boolean)
}