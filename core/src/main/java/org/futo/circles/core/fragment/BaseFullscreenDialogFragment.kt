package org.futo.circles.core.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.appbar.MaterialToolbar
import org.futo.circles.core.R
import org.futo.circles.core.extensions.onBackPressed


abstract class BaseFullscreenDialogFragment(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding
) : AppCompatDialogFragment() {

    private var _binding: ViewBinding? = null
    protected open val toolbarId = R.id.toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, requireContext().applicationInfo.theme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
    }

    protected fun getBinding() = _binding

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupToolbar() {
        view?.findViewById<MaterialToolbar>(toolbarId)?.let {
            it.setNavigationOnClickListener { onBackPressed() }
            it.navigationContentDescription = getString(R.string.back)
        }
    }
}