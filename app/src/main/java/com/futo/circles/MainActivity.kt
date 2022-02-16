package com.futo.circles

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.Navigation.findNavController
import com.futo.circles.provider.MatrixSessionProvider
import org.koin.android.ext.android.get


class MainActivity : AppCompatActivity(R.layout.main_activity) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setInitialFragment()
    }

    private fun setInitialFragment() {
        val navController: NavController = findNavController(this, R.id.nav_host_fragment)
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph_start_host)
        setStartDestination(navGraph)
        navController.setGraph(navGraph, null)
    }

    private fun setStartDestination(navGraph: NavGraph) {
        val startDestinationId = get<MatrixSessionProvider>().currentSession?.let {
            R.id.bottomNavigationFragment
        } ?: R.id.logInFragment

        navGraph.setStartDestination(startDestinationId)
    }
}