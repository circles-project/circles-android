package org.futo.circles.feature.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.futo.circles.R
import org.futo.circles.core.provider.MatrixSessionProvider

class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val destination = MatrixSessionProvider.currentSession?.let {
            SplashFragmentDirections.toBottomNavigationFragment()
        } ?: SplashFragmentDirections.toLogInFragment()

        findNavController().navigate(destination)
    }
}