package com.kyleriedemann.drinkingbuddy.di

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.kyleriedemann.drinkingbuddy.ui.dashboard.DashboardFragment
import com.kyleriedemann.drinkingbuddy.ui.dashboard.DashboardViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class DashboardModule {
    @ContributesAndroidInjector(modules = [DashboardViewModelModule::class])
    internal abstract fun dashboardFragment(): DashboardFragment
}

@Module
abstract class DashboardViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(DashboardViewModel::class)
    abstract fun bindFactory(factory: DashboardViewModel.Factory): ViewModelAssistedFactory<out ViewModel>

    @Binds
    abstract fun bindSavedStateRegistryOwner(dashboardFragment: DashboardFragment): SavedStateRegistryOwner

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideDefaultArgs(): Bundle? {
            return null
        }
    }
}