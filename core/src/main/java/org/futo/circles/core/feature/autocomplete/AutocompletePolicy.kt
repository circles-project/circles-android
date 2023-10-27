package org.futo.circles.core.feature.autocomplete

import android.text.Spannable

/**
 * This interface controls when to show or hide the popup window, and, in the first case,
 * what text should be passed to the popup [AutocompletePresenter].
 *
 * @see Autocomplete.SimplePolicy for the simplest possible implementation
 */
interface AutocompletePolicy {
    /**
     * Called to understand whether the popup should be shown. Some naive examples:
     * - Show when there's text: `return text.length() > 0`
     * - Show when last char is @: `return text.getCharAt(text.length()-1) == '@'`
     *
     * @param text current text, along with its Spans
     * @param cursorPos the position of the cursor
     * @return true if popup should be shown
     */
    fun shouldShowPopup(text: Spannable, cursorPos: Int): Boolean

    /**
     * Called to understand whether a currently shown popup should be closed, maybe
     * because text is invalid. A reasonable implementation is
     * `return !shouldShowPopup(text, cursorPos)`.
     *
     * However this is defined so you can add or clear spans.
     *
     * @param text current text, along with its Spans
     * @param cursorPos the position of the cursor
     * @return true if popup should be hidden
     */
    fun shouldDismissPopup(text: Spannable, cursorPos: Int): Boolean

    /**
     * Called to understand which query should be passed to [AutocompletePresenter]
     * for a showing popup. If this is called, [.shouldShowPopup] just returned
     * true, or [.shouldDismissPopup] just returned false.
     *
     * This is useful to understand which part of the text should be passed to presenters.
     * For example, user might have typed '@john' to select a username, but you just want to
     * search for 'john'.
     *
     * For more complex cases, you can add inclusive Spans in [.shouldShowPopup],
     * and get the span position here.
     *
     * @param text current text, along with its Spans
     * @return the query for presenter
     */
    fun getQuery(text: Spannable): CharSequence

    /**
     * Called when popup is dismissed. This can be used, for instance, to clear custom Spans
     * from the text.
     *
     * @param text text at the moment of dismissing
     */
    fun onDismiss(text: Spannable)
}