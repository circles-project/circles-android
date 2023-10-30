package org.futo.circles.core.feature.autocomplete

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import androidx.annotation.StyleRes
import androidx.core.view.ViewCompat
import androidx.core.widget.PopupWindowCompat
import kotlin.math.min

/**
 * A simplified version of andriod.widget.ListPopupWindow, which is the class used by
 * AutocompleteTextView.
 *
 * Other than being simplified, this deals with Views rather than ListViews, so the content
 * can be whatever. Lots of logic (clicks, selections etc.) has been removed because we manage that
 * in [AutocompletePresenter].
 *
 */
internal class AutocompletePopup(private val mContext: Context) {
    private var mView: ViewGroup? = null
    /**
     * @return The height of the popup window in pixels.
     */
    /**
     * Sets the height of the popup window in pixels. Can also be MATCH_PARENT.
     * @param height Height of the popup window.
     */
    @get:Suppress("unused")
    var height = ViewGroup.LayoutParams.WRAP_CONTENT
    /**
     * @return The width of the popup window in pixels.
     */
    /**
     * Sets the width of the popup window in pixels. Can also be MATCH_PARENT
     * or WRAP_CONTENT.
     * @param width Width of the popup window.
     */
    @get:Suppress("unused")
    var width = ViewGroup.LayoutParams.WRAP_CONTENT
    private var mMaxHeight = Int.MAX_VALUE
    private var mMaxWidth = Int.MAX_VALUE
    private var mUserMaxHeight = Int.MAX_VALUE
    private var mUserMaxWidth = Int.MAX_VALUE
    private var mHorizontalOffset = 0
    private var mVerticalOffset = 0
    private var mVerticalOffsetSet = false
    private var mGravity = Gravity.NO_GRAVITY
    /**
     * @return Whether the drop-down is visible under special conditions.
     */
    /**
     * Sets whether the drop-down should remain visible under certain conditions.
     *
     * The drop-down will occupy the entire screen below [.getAnchorView] regardless
     * of the size or content of the list.  [.getBackground] will fill any space
     * that is not used by the list.
     * @param dropDownAlwaysVisible Whether to keep the drop-down visible.
     */
    @get:Suppress("unused")
    @set:Suppress("unused")
    var isDropDownAlwaysVisible = false
    private var mOutsideTouchable = true

    /**
     * Returns the view that will be used to anchor this popup.
     * @return The popup's anchor view
     */
    var anchorView: View? = null
        private set
    private val mTempRect = Rect()
    private var mModal = false
    private val mPopup: PopupWindow = PopupWindow(mContext)

    /**
     * Create a new, empty popup window capable of displaying items from a ListAdapter.
     * Backgrounds should be set using [.setBackgroundDrawable].
     *
     * @param context Context used for contained views.
     */
    init {
        mPopup.inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
    }

    @get:Suppress("unused")
    var isModal: Boolean
        /**
         * Returns whether the popup window will be modal when shown.
         * @return `true` if the popup window will be modal, `false` otherwise.
         */
        get() = mModal
        /**
         * Set whether this window should be modal when shown.
         *
         *
         * If a popup window is modal, it will receive all touch and key input.
         * If the user touches outside the popup window's content area the popup window
         * will be dismissed.
         * @param modal `true` if the popup window should be modal, `false` otherwise.
         */
        set(modal) {
            mModal = modal
            mPopup.isFocusable = modal
        }

    fun setElevation(elevationPx: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) mPopup.elevation = elevationPx
    }

    var isOutsideTouchable: Boolean
        get() = mOutsideTouchable && !isDropDownAlwaysVisible
        set(outsideTouchable) {
            mOutsideTouchable = outsideTouchable
        }

    @get:Suppress("unused")
    var softInputMode: Int
        /**
         * Returns the current value in [.setSoftInputMode].
         * @see .setSoftInputMode
         * @see android.view.WindowManager.LayoutParams.softInputMode
         */
        get() = mPopup.softInputMode
        /**
         * Sets the operating mode for the soft input area.
         * @param mode The desired mode, see
         * [android.view.WindowManager.LayoutParams.softInputMode]
         * for the full list
         * @see android.view.WindowManager.LayoutParams.softInputMode
         *
         * @see .getSoftInputMode
         */
        set(mode) {
            mPopup.softInputMode = mode
        }

    @get:Suppress("unused")
    val background: Drawable?
        /**
         * @return The background drawable for the popup window.
         */
        get() = mPopup.background

    /**
     * Sets a drawable to be the background for the popup window.
     * @param d A drawable to set as the background.
     */
    fun setBackgroundDrawable(d: Drawable?) {
        mPopup.setBackgroundDrawable(d)
    }

    @get:StyleRes
    @get:Suppress("unused")
    @set:Suppress("unused")
    var animationStyle: Int
        /**
         * Returns the animation style that will be used when the popup window is
         * shown or dismissed.
         * @return Animation style that will be used.
         */
        get() = mPopup.animationStyle
        /**
         * Set an animation style to use when the popup window is shown or dismissed.
         * @param animationStyle Animation style to use.
         */
        set(animationStyle) {
            mPopup.animationStyle = animationStyle
        }

    /**
     * Sets the popup's anchor view. This popup will always be positioned relative to
     * the anchor view when shown.
     * @param anchor The view to use as an anchor.
     */
    fun setAnchorView(anchor: View) {
        anchorView = anchor
    }

    /**
     * Set the horizontal offset of this popup from its anchor view in pixels.
     * @param offset The horizontal offset of the popup from its anchor.
     */
    @Suppress("unused")
    fun setHorizontalOffset(offset: Int) {
        mHorizontalOffset = offset
    }

    /**
     * Set the vertical offset of this popup from its anchor view in pixels.
     * @param offset The vertical offset of the popup from its anchor.
     */
    fun setVerticalOffset(offset: Int) {
        mVerticalOffset = offset
        mVerticalOffsetSet = true
    }

    /**
     * Set the gravity of the dropdown list. This is commonly used to
     * set gravity to START or END for alignment with the anchor.
     * @param gravity Gravity value to use
     */
    fun setGravity(gravity: Int) {
        mGravity = gravity
    }

    /**
     * Sets the width of the popup window by the size of its content. The final width may be
     * larger to accommodate styled window dressing.
     * @param width Desired width of content in pixels.
     */
    @Suppress("unused")
    fun setContentWidth(contentWidth: Int) {
        var width = contentWidth
        val popupBackground = mPopup.background
        if (popupBackground != null) {
            popupBackground.getPadding(mTempRect)
            width += mTempRect.left + mTempRect.right
        }
        this.width = width
    }

    fun setMaxWidth(width: Int) {
        if (width > 0) {
            mUserMaxWidth = width
        }
    }

    /**
     * Sets the height of the popup window by the size of its content. The final height may be
     * larger to accommodate styled window dressing.
     * @param height Desired height of content in pixels.
     */
    @Suppress("unused")
    fun setContentHeight(contentHeight: Int) {
        var height = contentHeight
        val popupBackground = mPopup.background
        if (popupBackground != null) {
            popupBackground.getPadding(mTempRect)
            height += mTempRect.top + mTempRect.bottom
        }
        this.height = height
    }

    fun setMaxHeight(height: Int) {
        if (height > 0) {
            mUserMaxHeight = height
        }
    }

    fun setOnDismissListener(listener: PopupWindow.OnDismissListener?) {
        mPopup.setOnDismissListener(listener)
    }

    /**
     * Show the popup list. If the list is already showing, this method
     * will recalculate the popup's size and position.
     */
    fun show() {
        if (!ViewCompat.isAttachedToWindow(anchorView!!)) return
        val height = buildDropDown()
        val noInputMethod = isInputMethodNotNeeded
        val mDropDownWindowLayoutType = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL
        PopupWindowCompat.setWindowLayoutType(mPopup, mDropDownWindowLayoutType)
        if (mPopup.isShowing) {
            // First pass for this special case, don't know why.
            if (this.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                val tempWidth =
                    if (width == ViewGroup.LayoutParams.MATCH_PARENT) ViewGroup.LayoutParams.MATCH_PARENT else 0
                if (noInputMethod) {
                    mPopup.width = tempWidth
                    mPopup.height = 0
                } else {
                    mPopup.width = tempWidth
                    mPopup.height = ViewGroup.LayoutParams.MATCH_PARENT
                }
            }

            // The call to PopupWindow's update method below can accept -1
            // for any value you do not want to update.

            // Width.
            var widthSpec: Int
            widthSpec = if (width == ViewGroup.LayoutParams.MATCH_PARENT) {
                -1
            } else if (width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                anchorView!!.width
            } else {
                width
            }
            widthSpec = Math.min(widthSpec, mMaxWidth)
            widthSpec = if (widthSpec < 0) -1 else widthSpec

            // Height.
            var heightSpec: Int
            heightSpec = if (this.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                if (noInputMethod) height else ViewGroup.LayoutParams.MATCH_PARENT
            } else if (this.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                height
            } else {
                this.height
            }
            heightSpec = Math.min(heightSpec, mMaxHeight)
            heightSpec = if (heightSpec < 0) -1 else heightSpec

            // Update.
            mPopup.isOutsideTouchable = isOutsideTouchable
            if (heightSpec == 0) {
                dismiss()
            } else {
                mPopup.update(anchorView, mHorizontalOffset, mVerticalOffset, widthSpec, heightSpec)
            }
        } else {
            var widthSpec: Int
            widthSpec = if (width == ViewGroup.LayoutParams.MATCH_PARENT) {
                ViewGroup.LayoutParams.MATCH_PARENT
            } else if (width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                anchorView!!.width
            } else {
                width
            }
            widthSpec = Math.min(widthSpec, mMaxWidth)
            var heightSpec: Int
            heightSpec = if (this.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                ViewGroup.LayoutParams.MATCH_PARENT
            } else if (this.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                height
            } else {
                this.height
            }
            heightSpec = Math.min(heightSpec, mMaxHeight)

            // Set width and height.
            mPopup.width = widthSpec
            mPopup.height = heightSpec
            mPopup.isClippingEnabled = true

            // use outside touchable to dismiss drop down when touching outside of it, so
            // only set this if the dropdown is not always visible
            mPopup.isOutsideTouchable = isOutsideTouchable
            PopupWindowCompat.showAsDropDown(
                mPopup,
                anchorView!!, mHorizontalOffset, mVerticalOffset, mGravity
            )
        }
    }

    /**
     * Dismiss the popup window.
     */
    fun dismiss() {
        mPopup.dismiss()
        mPopup.contentView = null
        mView = null
    }

    /**
     * Control how the popup operates with an input method: one of
     * INPUT_METHOD_FROM_FOCUSABLE, INPUT_METHOD_NEEDED,
     * or INPUT_METHOD_NOT_NEEDED.
     *
     *
     * If the popup is showing, calling this method will take effect only
     * the next time the popup is shown or through a manual call to the [.show]
     * method.
     *
     * @see .show
     */
    fun setInputMethodMode(mode: Int) {
        mPopup.inputMethodMode = mode
    }

    val isShowing: Boolean
        /**
         * @return `true` if the popup is currently showing, `false` otherwise.
         */
        get() = mPopup.isShowing
    val isInputMethodNotNeeded: Boolean
        /**
         * @return `true` if this popup is configured to assume the user does not need
         * to interact with the IME while it is showing, `false` otherwise.
         */
        get() = mPopup.inputMethodMode == PopupWindow.INPUT_METHOD_NOT_NEEDED

    fun setView(view: ViewGroup?) {
        mView = view
        mView!!.isFocusable = true
        mView!!.isFocusableInTouchMode = true
        val dropDownView = mView
        mPopup.contentView = dropDownView
        val params = mView!!.layoutParams
        if (params != null) {
            if (params.height > 0) height = params.height
            if (params.width > 0) width = params.width
        }
    }

    /**
     *
     * Builds the popup window's content and returns the height the popup
     * should have. Returns -1 when the content already exists.
     *
     * @return the content's wrap content height or -1 if content already exists
     */
    private fun buildDropDown(): Int {
        var otherHeights = 0

        // getMaxAvailableHeight() subtracts the padding, so we put it back
        // to get the available height for the whole window.
        val paddingVert: Int
        val paddingHoriz: Int
        val background = mPopup.background
        if (background != null) {
            background.getPadding(mTempRect)
            paddingVert = mTempRect.top + mTempRect.bottom
            paddingHoriz = mTempRect.left + mTempRect.right

            // If we don't have an explicit vertical offset, determine one from
            // the window background so that content will line up.
            if (!mVerticalOffsetSet) {
                mVerticalOffset = -mTempRect.top
            }
        } else {
            mTempRect.setEmpty()
            paddingVert = 0
            paddingHoriz = 0
        }

        // Redefine dimensions taking into account maxWidth and maxHeight.
        val ignoreBottomDecorations = mPopup.inputMethodMode == PopupWindow.INPUT_METHOD_NOT_NEEDED
        val maxContentHeight =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) mPopup.getMaxAvailableHeight(
                anchorView!!, mVerticalOffset, ignoreBottomDecorations
            ) else mPopup.getMaxAvailableHeight(
                anchorView!!, mVerticalOffset
            )
        val maxContentWidth = mContext.resources.displayMetrics.widthPixels - paddingHoriz
        mMaxHeight = min(maxContentHeight + paddingVert, mUserMaxHeight)
        mMaxWidth = min(maxContentWidth + paddingHoriz, mUserMaxWidth)
        // if (mHeight > 0) mHeight = Math.min(mHeight, maxContentHeight);
        // if (mWidth > 0) mWidth = Math.min(mWidth, maxContentWidth);
        if (isDropDownAlwaysVisible || height == ViewGroup.LayoutParams.MATCH_PARENT) {
            return mMaxHeight
        }
        val childWidthSpec: Int = when (width) {
            ViewGroup.LayoutParams.WRAP_CONTENT -> View.MeasureSpec.makeMeasureSpec(
                maxContentWidth,
                View.MeasureSpec.AT_MOST
            )

            ViewGroup.LayoutParams.MATCH_PARENT -> View.MeasureSpec.makeMeasureSpec(
                maxContentWidth,
                View.MeasureSpec.EXACTLY
            )

            else -> View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
        }

        // Add padding only if the list has items in it, that way we don't show
        // the popup if it is not needed. For this reason, we measure as wrap_content.
        mView!!.measure(
            childWidthSpec,
            View.MeasureSpec.makeMeasureSpec(maxContentHeight, View.MeasureSpec.AT_MOST)
        )
        val viewHeight = mView!!.measuredHeight
        if (viewHeight > 0) {
            otherHeights += paddingVert + mView!!.paddingTop + mView!!.paddingBottom
        }
        return Math.min(viewHeight + otherHeights, mMaxHeight)
    }
}