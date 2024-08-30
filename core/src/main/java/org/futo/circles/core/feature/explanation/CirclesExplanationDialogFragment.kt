package org.futo.circles.core.feature.explanation

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import org.futo.circles.core.R
import org.futo.circles.core.databinding.DialogCirclesExplanationBinding


class CirclesExplanationDialogFragment : AppCompatDialogFragment(), ExplanationDismissListener {

    private val explanationFragment by lazy { CircleExplanationFragment() }

    override fun onStart() {
        super.onStart()
        dialog?.setCancelable(false)
        dialog?.window?.apply {
            setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 20))
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setGravity(Gravity.TOP)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = DialogCirclesExplanationBinding.inflate(inflater, container, false).root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) addCirclesExplanationFragment()
    }


    private fun addCirclesExplanationFragment() {
        childFragmentManager
            .beginTransaction()
            .replace(R.id.lContainer, explanationFragment)
            .commitAllowingStateLoss()
    }

    override fun onDismissPopUp() {
        dismiss()
    }

}