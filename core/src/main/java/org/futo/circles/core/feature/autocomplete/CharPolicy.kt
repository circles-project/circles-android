package org.futo.circles.core.feature.autocomplete

import android.text.Spannable
import android.text.Spanned
import android.util.Log

/**
 * A special [AutocompletePolicy] for cases when you want to trigger the popup when a
 * certain character is shown.
 *
 * For instance, this might be the case for hashtags ('#') or usernames ('@') or whatever you wish.
 * Passing this to [Autocomplete.Builder] ensures the following behavior (assuming '@'):
 * - text "@john" : presenter will be passed the query "john"
 * - text "You should see this @j" : presenter will be passed the query "j"
 * - text "You should see this @john @m" : presenter will be passed the query "m"
 */
class CharPolicy : AutocompletePolicy {
    private val CH: Char
    private val INT = IntArray(2)
    private var needSpaceBefore = true

    /**
     * Constructs a char policy for the given character.
     *
     * @param trigger the triggering character.
     */
    constructor(trigger: Char) {
        CH = trigger
    }

    /**
     * Constructs a char policy for the given character.
     * You can choose whether a whitespace is needed before 'trigger'.
     *
     * @param trigger the triggering character.
     * @param needSpaceBefore whether we need a space before trigger
     */
    @Suppress("unused")
    constructor(trigger: Char, needSpaceBefore: Boolean) {
        CH = trigger
        this.needSpaceBefore = needSpaceBefore
    }

    /**
     * Can be overriden to understand which characters are valid. The default implementation
     * returns true for any character except whitespaces.
     *
     * @param ch the character
     * @return whether it's valid part of a query
     */
    protected fun isValidChar(ch: Char): Boolean {
        return !Character.isWhitespace(ch)
    }

    private fun checkText(text: Spannable, cursorPosParam: Int): IntArray? {
        var cursorPos = cursorPosParam
        val spanEnd = cursorPos
        var last = 'x'
        cursorPos -= 1 // If the cursor is at the end, we will have cursorPos = length. Go back by 1.
        while (cursorPos >= 0 && last != CH) {
            val ch = text[cursorPos]
            log("checkText: char is $ch")
            if (isValidChar(ch)) {
                // We are going back
                log("checkText: char is valid")
                cursorPos -= 1
                last = ch
            } else {
                // We got a whitespace before getting a CH. This is invalid.
                log("checkText: char is not valid, returning NULL")
                return null
            }
        }
        cursorPos += 1 // + 1 because we end BEHIND the valid selection

        // Start checking.
        if (cursorPos == 0 && last != CH) {
            // We got to the start of the string, and no CH was encountered. Nothing to do.
            log("checkText: got to start but no CH, returning NULL")
            return null
        }

        // Additional checks for cursorPos - 1
        if (cursorPos > 0 && needSpaceBefore) {
            val ch = text[cursorPos - 1]
            if (!Character.isWhitespace(ch)) {
                log("checkText: char before is not whitespace, returning NULL")
                return null
            }
        }

        // All seems OK.
        val spanStart = cursorPos + 1 // + 1 because we want to exclude CH from the query
        INT[0] = spanStart
        INT[1] = spanEnd
        log("checkText: found! cursorPos=$cursorPos")
        log("checkText: found! spanStart=$spanStart")
        log("checkText: found! spanEnd=$spanEnd")
        return INT
    }

    override fun shouldShowPopup(text: Spannable, cursorPos: Int): Boolean {
        // Returning true if, right before cursorPos, we have a word starting with @.
        log("shouldShowPopup: text is $text")
        log("shouldShowPopup: cursorPos is $cursorPos")
        val show = checkText(text, cursorPos)
        if (show != null) {
            text.setSpan(QuerySpan(), show[0], show[1], Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            return true
        }
        log("shouldShowPopup: returning false")
        return false
    }

    override fun shouldDismissPopup(text: Spannable, cursorPos: Int): Boolean {
        log("shouldDismissPopup: text is $text")
        log("shouldDismissPopup: cursorPos is $cursorPos")
        val dismiss = checkText(text, cursorPos) == null
        log("shouldDismissPopup: returning $dismiss")
        return dismiss
    }

    override fun getQuery(text: Spannable): CharSequence {
        val span = text.getSpans(0, text.length, QuerySpan::class.java)
        if (span == null || span.size == 0) {
            // Should never happen.
            log("getQuery: there's no span!")
            return ""
        }
        log("getQuery: found spans: " + span.size)
        val sp = span[0]
        log("getQuery: span start is " + text.getSpanStart(sp))
        log("getQuery: span end is " + text.getSpanEnd(sp))
        val seq = text.subSequence(text.getSpanStart(sp), text.getSpanEnd(sp))
        log("getQuery: returning $seq")
        return seq
    }

    override fun onDismiss(text: Spannable) {
        // Remove any span added by shouldShow. Should be useless, but anyway.
        val span = text.getSpans(0, text.length, QuerySpan::class.java)
        for (s in span) {
            text.removeSpan(s)
        }
    }

    private class QuerySpan
    companion object {
        private val TAG = CharPolicy::class.java.simpleName
        private const val DEBUG = false
        private fun log(log: String) {
            if (DEBUG) Log.e(TAG, log)
        }

        /**
         * Returns the current query out of the given Spannable.
         * @param text the anchor text
         * @return an int[] with query start and query end positions
         */
        fun getQueryRange(text: Spannable): IntArray? {
            val span = text.getSpans(0, text.length, QuerySpan::class.java)
            if (span == null || span.size == 0) return null
            if (span.size > 1) {
                // Won't happen
                log("getQueryRange:  ERR: MORE THAN ONE QuerySpan.")
            }
            val sp = span[0]
            return intArrayOf(text.getSpanStart(sp), text.getSpanEnd(sp))
        }
    }
}