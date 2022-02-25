package com.futo.circles

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.futo.circles.provider.MatrixSessionProvider


class MainActivity : AppCompatActivity(R.layout.main_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setInitialFragment()
    }

    private fun setInitialFragment() {
        val navController: NavController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph_start_host)
        setStartDestination(navGraph)
        navController.setGraph(navGraph, intent.extras)
    }

    private fun setStartDestination(navGraph: NavGraph) {
        val startDestinationId = MatrixSessionProvider.currentSession?.let {
            R.id.bottomNavigationFragment
        } ?: R.id.logInFragment

        navGraph.setStartDestination(startDestinationId)
    }
}