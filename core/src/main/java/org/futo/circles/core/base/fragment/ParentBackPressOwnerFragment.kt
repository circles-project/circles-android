package org.futo.circles.core.base.fragment

import android.content.Context
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

interface BackPressOwner {
    fun onChildBackPress(callback: OnBackPressedCallback)
}

abstract class ParentBackPressOwnerFragment(@LayoutRes contentLayoutId: Int) :
    Fragment(contentLayoutId) {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = (parentFragment?.parentFragment as? BackPressOwner)
            ?: (parentFragment as? BackPressOwner) ?: return

        activity?.onBackPressedDispatcher?.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    parent.onChildBackPress(this)
                }
            })
    }
}
