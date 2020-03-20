package com.kyleriedemann.drinkingbuddy

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.github.ajalt.timberkt.Timber.v
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kyleriedemann.drinkingbuddy.data.source.NotificationRepository
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    @Inject lateinit var notificationRepository: NotificationRepository

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
            v { "controller: [$controller], destination: [$destination], arguments: [$arguments]" }
        }

        lifecycleScope.launch {
            notificationRepository.getUnreadNotificationCount().collect {
                val badge = navView.getOrCreateBadge(R.id.navigation_notifications)
                badge.number = it

                if (it > 0) badge.setVisible(true, true)
                else badge.isVisible = false
            }
        }

        when (intent.extras?.getString(destinationKey) ?: "") {
            logDestination -> navController.navigate(R.id.action_global_logListFragment)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    companion object {
        const val destinationKey = "destinationKey"
        const val logDestination = "logs"

        fun logsIntent(context: Context): Intent {
            val intent = Intent(context, MainActivity::class.java)
            intent.action = Intent.ACTION_MAIN
            intent.putExtra(destinationKey, logDestination)
            return intent
        }
    }
}
