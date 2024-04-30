package org.futo.circles.core.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import com.google.android.material.button.MaterialButton
import org.futo.circles.core.base.NetworkObserver

class NetworkRequiredButton(
    context: Context,
    attrs: AttributeSet? = null
) : MaterialButton(context, attrs), OnClickListener {

    private var customClickListener: OnClickListener? = null

    override fun setOnClickListener(l: OnClickListener?) {
        customClickListener = l
    }

    override fun onClick(v: View?) {
        if (!NetworkObserver.isConnected()) return
        customClickListener?.onClick(v)
    }
}