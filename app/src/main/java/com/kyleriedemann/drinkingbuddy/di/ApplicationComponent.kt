package com.kyleriedemann.drinkingbuddy.di

import android.app.Application
import com.kyleriedemann.drinkingbuddy.DrinkingBuddyApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

/**
 * Main component for the application.
 *
 * See the `TestApplicationComponent` used in UI tests.
 */
@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ApplicationModule::class,
        MainActivityModule::class,
        HomeModule::class,
        DashboardModule::class,
        NotificationsModule::class,
        NotificationDetailModule::class,
        ViewModelAssistedFactoriesModule::class,
        LogModule::class,
        LogListModule::class
    ])
interface ApplicationComponent : AndroidInjector<DrinkingBuddyApplication> {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Application): ApplicationComponent
    }
}