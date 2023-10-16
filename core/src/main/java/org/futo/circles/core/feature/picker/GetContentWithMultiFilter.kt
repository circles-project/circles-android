package org.futo.circles.core.feature.picker

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts

class GetContentWithMultiFilter : ActivityResultContracts.GetContent() {
    override fun createIntent(context: Context, input: String): Intent {
        val inputArray = input.split(";").toTypedArray()
        val myIntent = super.createIntent(context, "*/*")
        myIntent.putExtra(Intent.EXTRA_MIME_TYPES, inputArray)
        return myIntent
    }
}