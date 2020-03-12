package com.kyleriedemann.drinkingbuddy

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            Timber.v("controller: [$controller], destination: [$destination], arguments: [$arguments]")
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        Timber.v("navigating up")
        val navController = findNavController(R.id.nav_host_fragment)
        Timber.v("$navController")
//        Timber.v("${navController.navigateUp()}")
//        Timber.v("${navController.navigateUp(appBarConfiguration)}")
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
