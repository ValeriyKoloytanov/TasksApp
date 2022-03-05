package com.koloytanov.tasksApp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI


class MainActivity : AppCompatActivity() {
    private lateinit var navHostFragment: NavHostFragment
    private var fromRecycleView = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment

        val listener =  NavController.OnDestinationChangedListener { controller, destination, arguments ->
                when (destination.id) {
                    R.id.toDoListFragment -> {
                        if (arguments != null) {
                            if (arguments.getBoolean("from_recycle_view")) {
                                fromRecycleView = true
                                val graph = controller.graph.findNode(R.id.nested_graph) as NavGraph
                                graph.setStartDestination(R.id.detailedTaskFragment)
                            } else {
                                val graph = controller.graph.findNode(R.id.nested_graph) as NavGraph
                                graph.setStartDestination(R.id.addTaskFragment)
                                fromRecycleView = false
                            }
                        }
                    }
                    R.id.addTaskFragment -> {
                        if (fromRecycleView)
                            destination.label = "Обновить задачу"
                        else {
                            destination.label = "Добавить задачу"

                        }
                        fromRecycleView = false

                    }

                }


            }
        navHostFragment.navController.addOnDestinationChangedListener(listener)
        NavigationUI.setupActionBarWithNavController(this, navHostFragment.navController)


    }
    override fun onBackPressed() {
          onSupportNavigateUp()


    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment_container)
        if (navController.currentDestination?.id == R.id.detailedTaskFragment) {
            navController.setGraph(R.navigation.nav_graph, bundleOf("from_recycle_view" to false))
        }

        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}