package com.futo.circles.base


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.viewbinding.ViewBinding
import com.futo.circles.R


abstract class BaseFullscreenDialogFragment(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding
) : AppCompatDialogFragment() {

    private var _binding: ViewBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Theme_Circles)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return _binding?.root
    }

    protected fun getBinding() = _binding

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}