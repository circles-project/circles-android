package com.futo.circles.feature.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.futo.circles.R
import com.futo.circles.provider.MatrixSessionProvider

class SplashFragment : Fragment(R.layout.splash_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val destination = MatrixSessionProvider.currentSession?.let {
            SplashFragmentDirections.toBottomNavigationFragment()
        } ?: SplashFragmentDirections.toLogInFragment()

        findNavController().navigate(destination)
    }
}