package org.futo.circles.feature.explanation

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.navigation.fragment.navArgs
import org.futo.circles.R
import org.futo.circles.auth.feature.explanation.CircleExplanationFragment
import org.futo.circles.auth.feature.explanation.ExplanationDismissListener
import org.futo.circles.databinding.DialogCirclesExplanationBinding


class CirclesExplanationDialogFragment : AppCompatDialogFragment(), ExplanationDismissListener {

    private val args: CirclesExplanationDialogFragmentArgs by navArgs()

    private val explanationFragment by lazy { CircleExplanationFragment.create(args.type) }


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