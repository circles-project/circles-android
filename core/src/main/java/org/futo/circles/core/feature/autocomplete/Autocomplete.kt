package org.futo.circles.core.feature.autocomplete

import android.database.DataSetObserver
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.Selection
import android.text.SpanWatcher
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.widget.EditText
import android.widget.PopupWindow
import org.futo.circles.core.feature.autocomplete.Autocomplete.SimplePolicy

/**
 * Entry point for adding Autocomplete behavior to a [EditText].
 *
 * You can construct a `Autocomplete` using the builder provided by [Autocomplete.on].
 * Building is enough, but you can hold a reference to this class to call its public methods.
 *
 * Requires:
 * - [EditText]: this is both the anchor for the popup, and the source of text events that we listen to
 * - [AutocompletePresenter]: this presents items in the popup window. See class for more info.
 * - [AutocompleteCallback]: if specified, this listens to click events and visibility changes
 * - [AutocompletePolicy]: if specified, this controls how and when to show the popup based on text events
 * If not, this defaults to [SimplePolicy]: shows the popup when text.length() bigger than 0.
 */
class Autocomplete<T> private constructor(builder: Builder<T>) : TextWatcher,
    SpanWatcher {
    /**
     * Builder for building [Autocomplete].
     * The only mandatory item is a presenter, [.with].
     *
     * @param <T> the data model
    </T> */
    class Builder<T>(source: EditText) {
        internal var source: EditText?
        internal var presenter: AutocompletePresenter<T>? = null
        internal var policy: AutocompletePolicy? = null
        internal var callback: AutocompleteCallback<T>? = null
        internal var backgroundDrawable: Drawable? = null
        internal var elevationDp = 6f

        init {
            this.source = source
        }

        /**
         * Registers the [AutocompletePresenter] to be used, responsible for showing
         * items. See the class for info.
         *
         * @param presenter desired presenter
         * @return this for chaining
         */
        fun with(presenter: AutocompletePresenter<T>): Builder<T> {
            this.presenter = presenter
            return this
        }

        /**
         * Registers the [AutocompleteCallback] to be used, responsible for listening to
         * clicks provided by the presenter, and visibility changes.
         *
         * @param callback desired callback
         * @return this for chaining
         */
        fun with(callback: AutocompleteCallback<T>): Builder<T> {
            this.callback = callback
            return this
        }

        /**
         * Registers the [AutocompletePolicy] to be used, responsible for showing / dismissing
         * the popup when certain events happen (e.g. certain characters are typed).
         *
         * @param policy desired policy
         * @return this for chaining
         */
        fun with(policy: AutocompletePolicy?): Builder<T> {
            this.policy = policy
            return this
        }

        /**
         * Sets a background drawable for the popup.
         *
         * @param backgroundDrawable drawable
         * @return this for chaining
         */
        fun with(backgroundDrawable: Drawable?): Builder<T> {
            this.backgroundDrawable = backgroundDrawable
            return this
        }

        /**
         * Sets elevation for the popup. Defaults to 6 dp.
         *
         * @param elevationDp popup elevation, in DP
         * @return this for chaning.
         */
        fun with(elevationDp: Float): Builder<T> {
            this.elevationDp = elevationDp
            return this
        }

        /**
         * Builds an Autocomplete instance. This is enough for autocomplete to be set up,
         * but you can hold a reference to the object and call its public methods.
         *
         * @return an Autocomplete instance, if you need it
         *
         * @throws RuntimeException if either EditText or the presenter are null
         */
        fun build(): Autocomplete<T> {
            if (source == null) throw RuntimeException("Autocomplete needs a source!")
            if (presenter == null) throw RuntimeException("Autocomplete needs a presenter!")
            if (policy == null) policy = SimplePolicy()
            return Autocomplete(this)
        }

        fun clear() {
            source = null
            presenter = null
            callback = null
            policy = null
            backgroundDrawable = null
            elevationDp = 6f
        }
    }

    private val policy: AutocompletePolicy
    private val popup: AutocompletePopup
    private val presenter: AutocompletePresenter<T>
    private val callback: AutocompleteCallback<T>?
    private val source: EditText
    private var block = false
    private var disabled = false
    private var openBefore = false
    private var lastQuery = "null"

    init {
        policy = builder.policy!!
        presenter = builder.presenter!!
        callback = builder.callback
        source = builder.source!!

        // Set up popup
        popup = AutocompletePopup(source.context)
        popup.setAnchorView(source)
        popup.setGravity(Gravity.START)
        popup.isModal = false
        popup.setBackgroundDrawable(builder.backgroundDrawable)
        popup.setElevation(
            TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, builder.elevationDp,
                source.context.resources.displayMetrics
            )
        )

        // popup dimensions
        val dim: AutocompletePresenter.PopupDimensions = presenter.popupDimensions
        popup.width = dim.width
        popup.height = dim.height
        popup.setMaxWidth(dim.maxWidth)
        popup.setMaxHeight(dim.maxHeight)

        // Fire visibility events
        popup.setOnDismissListener(PopupWindow.OnDismissListener {
            lastQuery = "null"
            callback?.onPopupVisibilityChanged(false)
            val saved = block
            block = true
            policy.onDismiss(source.text)
            block = saved
            presenter.hideView()
        })

        // Set up source
        source.text.setSpan(this, 0, source.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        source.addTextChangedListener(this)

        // Set up presenter
        presenter.registerClickProvider(object : AutocompletePresenter.ClickProvider<T> {
            override fun click(item: T) {
                val callback: AutocompleteCallback<T>? = callback
                val edit = source
                if (callback == null) return
                val saved = block
                block = true
                val dismiss: Boolean = callback.onPopupItemClicked(edit.text, item)
                if (dismiss) dismissPopup()
                block = saved
            }
        })
        builder.clear()
    }

    /**
     * Controls how the popup operates with an input method.
     *
     * If the popup is showing, calling this method will take effect only
     * the next time the popup is shown.
     *
     * @param mode a [PopupWindow] input method mode
     */
    fun setInputMethodMode(mode: Int) {
        popup.setInputMethodMode(mode)
    }

    /**
     * Sets the operating mode for the soft input area.
     *
     * @param mode The desired mode, see [WindowManager.LayoutParams.softInputMode]
     */
    fun setSoftInputMode(mode: Int) {
        popup.softInputMode = mode
    }

    /**
     * Shows the popup with the given query.
     * There is rarely need to call this externally: it is already triggered by events on the anchor.
     * To control when this is called, provide a good implementation of [AutocompletePolicy].
     *
     * @param query query text.
     */
    fun showPopup(query: CharSequence) {
        if (isPopupShowing && lastQuery == query.toString()) return
        lastQuery = query.toString()
        log("showPopup: called with filter $query")
        if (!isPopupShowing) {
            log("showPopup: showing")
            presenter.registerDataSetObserver(Observer()) // Calling new to avoid leaking... maybe...
            popup.setView(presenter.view)
            presenter.showView()
            popup.show()
            callback?.onPopupVisibilityChanged(true)
        }
        log("showPopup: popup should be showing... $isPopupShowing")
        presenter.onQuery(query)
    }

    /**
     * Dismisses the popup, if showing.
     * There is rarely need to call this externally: it is already triggered by events on the anchor.
     * To control when this is called, provide a good implementation of [AutocompletePolicy].
     */
    fun dismissPopup() {
        if (isPopupShowing) {
            popup.dismiss()
        }
    }

    val isPopupShowing: Boolean
        /**
         * Returns true if the popup is showing.
         * @return whether the popup is currently showing
         */
        get() = popup.isShowing

    /**
     * Switch to control the autocomplete behavior. When disabled, no popup is shown.
     * This is useful if you want to do runtime edits to the anchor text, without triggering
     * the popup.
     *
     * @param enabled whether to enable autocompletion
     */
    fun setEnabled(enabled: Boolean) {
        disabled = !enabled
    }

    /**
     * Sets the gravity for the popup. Basically only [Gravity.START] and [Gravity.END]
     * do work.
     *
     * @param gravity gravity for the popup
     */
    fun setGravity(gravity: Int) {
        popup.setGravity(gravity)
    }

    /**
     * Controls the vertical offset of the popup from the EditText anchor.
     *
     * @param offset offset in pixels.
     */
    fun setOffsetFromAnchor(offset: Int) {
        popup.setVerticalOffset(offset)
    }

    /**
     * Controls whether the popup should listen to clicks outside its boundaries.
     *
     * @param outsideTouchable true to listen to outside clicks
     */
    fun setOutsideTouchable(outsideTouchable: Boolean) {
        popup.isOutsideTouchable = outsideTouchable
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        if (block || disabled) return
        openBefore = isPopupShowing
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (block || disabled) return
        if (openBefore && !isPopupShowing) {
            return  // Copied from somewhere.
        }
        if (s !is Spannable) {
            source.setText(SpannableString(s))
            return
        }
        val sp = s
        val cursor = source.selectionEnd
        log("onTextChanged: cursor end position is $cursor")
        if (cursor == -1) { // No cursor present.
            dismissPopup()
            return
        }
        val b = block
        block = true // policy might add spans or other stuff.
        if (isPopupShowing && policy.shouldDismissPopup(sp, cursor)) {
            log("onTextChanged: dismissing")
            dismissPopup()
        } else if (isPopupShowing || policy.shouldShowPopup(sp, cursor)) {
            // LOG.now("onTextChanged: updating with filter "+policy.getQuery(sp));
            showPopup(policy.getQuery(sp))
        }
        block = b
    }

    override fun afterTextChanged(s: Editable) {}
    override fun onSpanAdded(text: Spannable, what: Any, start: Int, end: Int) {}
    override fun onSpanRemoved(text: Spannable, what: Any, start: Int, end: Int) {}
    override fun onSpanChanged(
        text: Spannable,
        what: Any,
        ostart: Int,
        oend: Int,
        nstart: Int,
        nend: Int
    ) {
        if (disabled || block) return
        if (what === Selection.SELECTION_END) {
            // Selection end changed from ostart to nstart. Trigger a check.
            log("onSpanChanged: selection end moved from $ostart to $nstart")
            log("onSpanChanged: block is $block")
            val b = block
            block = true
            if (!isPopupShowing && policy.shouldShowPopup(text, nstart)) {
                showPopup(policy.getQuery(text))
            }
            block = b
        }
    }

    private inner class Observer : DataSetObserver(), Runnable {
        private val ui = Handler(Looper.getMainLooper())
        override fun onChanged() {
            // ??? Not sure this is needed...
            ui.post(this)
        }

        override fun run() {
            if (isPopupShowing) {
                // Call show again to revisit width and height.
                popup.show()
            }
        }
    }

    /**
     * A very simple [AutocompletePolicy] implementation.
     * Popup is shown when text length is bigger than 0, and hidden when text is empty.
     * The query string is the whole text.
     */
    class SimplePolicy : AutocompletePolicy {
        override fun shouldShowPopup(text: Spannable, cursorPos: Int): Boolean {
            return text.length > 0
        }

        override fun shouldDismissPopup(text: Spannable, cursorPos: Int): Boolean {
            return text.length == 0
        }

        override fun getQuery(text: Spannable): CharSequence {
            return text
        }

        override fun onDismiss(text: Spannable) {}
    }

    companion object {
        private val TAG = Autocomplete::class.java.simpleName
        private const val DEBUG = false
        private fun log(log: String) {
            if (DEBUG) Log.e(TAG, log)
        }

        /**
         * Entry point for building autocomplete on a certain [EditText].
         * @param anchor the anchor for the popup, and the source of text events
         * @param <T> your data model
         * @return a Builder for set up
        </T> */
        fun <T> on(anchor: EditText): Builder<T> {
            return Builder<T>(anchor)
        }
    }
}