package com.kyleriedemann.drinkingbuddy.di

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.kyleriedemann.drinkingbuddy.ui.home.HomeFragment
import com.kyleriedemann.drinkingbuddy.ui.home.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class HomeModule {
    @ContributesAndroidInjector(modules = [HomeViewModelModule::class])
    internal abstract fun homeFragment(): HomeFragment
}

@Module
abstract class HomeViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    abstract fun bindFactory(factory: HomeViewModel.Factory): ViewModelAssistedFactory<out ViewModel>

    @Binds
    abstract fun bindSavedStateRegistryOwner(homeFragment: HomeFragment): SavedStateRegistryOwner

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideDefaultArgs(): Bundle? {
            return null
        }
    }
}