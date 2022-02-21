package com.futo.circles.extensions

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.futo.circles.R
import com.google.android.material.snackbar.Snackbar


@SuppressLint("InflateParams")
fun Fragment.showError(message: String) {
    view?.let {
        val snack: Snackbar = Snackbar.make(it, message, Snackbar.LENGTH_LONG)
        val customSnackView = layoutInflater.inflate(R.layout.error_snack_bar_view, null)
        snack.view.setBackgroundColor(Color.TRANSPARENT)

        val snackLayout = snack.view as Snackbar.SnackbarLayout
        snackLayout.setPadding(0, 0, 0, 0)

        customSnackView.findViewById<TextView>(R.id.tvErrorMessage).also { textView ->
            textView.text = message
        }
        snackLayout.addView(customSnackView, 0)

        val layoutParams = (snack.view.layoutParams as? FrameLayout.LayoutParams)?.also { params ->
            params.gravity = Gravity.TOP
        }
        snack.view.layoutParams = layoutParams
        snack.show()
    }
}

fun Fragment.setEnabledViews(enabled: Boolean) {
    (view?.rootView as? ViewGroup)?.children?.forEach {
        if (it.isClickable) it.isEnabled = enabled
        (it as? ViewGroup)?.setEnabledChildren(enabled)
    }
}

fun ViewGroup.setEnabledChildren(enabled: Boolean) {
    children.forEach {
        if (it.isClickable) it.isEnabled = enabled
        (it as? ViewGroup)?.setEnabledChildren(enabled)
    }
}

fun Fragment.setSupportActionBar(toolbar: Toolbar) {
    (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
}

fun Fragment.setToolbarTitle(title: String) {
    (activity as? AppCompatActivity)?.supportActionBar?.title = title
}