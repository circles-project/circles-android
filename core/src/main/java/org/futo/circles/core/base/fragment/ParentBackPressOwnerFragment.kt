package org.futo.circles.core.base.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.viewbinding.ViewBinding

interface BackPressOwner {
    fun onChildBackPress(callback: OnBackPressedCallback)
}

abstract class ParentBackPressOwnerFragment<VB : ViewBinding>(
    inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : BaseBindingFragment<VB>(inflate) {

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
