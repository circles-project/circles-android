package org.futo.circles.feature.bottom_navigation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.databinding.BottomNavigationFragmentBinding
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.setSupportActionBar
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class BottomNavigationFragment : Fragment(R.layout.bottom_navigation_fragment) {

    private val binding by viewBinding(BottomNavigationFragmentBinding::bind)
    private val systemNoticesCountViewModel by sharedViewModel<SystemNoticesCountSharedViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findChildNavController()?.let { controller ->
            binding.bottomNavigationView.setupWithNavController(controller)
            setupToolBar(controller)
        }
        setupObservers()
    }

    private fun setupObservers() {
        systemNoticesCountViewModel.systemNoticesCountLiveData?.observeData(this) {
            val count = it ?: 0
            binding.bottomNavigationView.getOrCreateBadge(R.id.settings_nav_graph).apply {
                isVisible = count > 0
                number = count
            }
        }
    }

    private fun findChildNavController() =
        (childFragmentManager.findFragmentById(R.id.bottom_nav_host_fragment) as? NavHostFragment)?.navController

    private fun setupToolBar(navController: NavController) {
        setSupportActionBar(binding.toolbar)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.settingsFragment,
                R.id.circlesFragment,
                R.id.peopleFragment,
                R.id.groupsFragment,
                R.id.photosFragment
            )
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }
}