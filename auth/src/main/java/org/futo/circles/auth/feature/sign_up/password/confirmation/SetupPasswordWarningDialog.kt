package org.futo.circles.auth.feature.sign_up.password.confirmation

import android.app.ActionBar
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialog
import org.futo.circles.auth.databinding.DialogSetupPasswordWarningBinding

class SetupPasswordWarningDialog(context: Context) :
    AppCompatDialog(context) {

    private val binding = DialogSetupPasswordWarningBinding.inflate(LayoutInflater.from(context))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setCancelable(false)
        window?.apply {
            setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 20))
            setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)
        }
        binding.btnUnderstand.setOnClickListener { dismiss() }
    }
}