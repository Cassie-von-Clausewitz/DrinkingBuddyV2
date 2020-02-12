package com.kyleriedemann.drinkingbuddy.di

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.kyleriedemann.drinkingbuddy.ui.notifications.NotificationsFragment
import com.kyleriedemann.drinkingbuddy.ui.notifications.NotificationsViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class NotificationsModule {
    @ContributesAndroidInjector(modules = [NotificationsViewModelModule::class])
    internal abstract fun notificationsFragment(): NotificationsFragment
}

@Module
abstract class NotificationsViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(NotificationsViewModel::class)
    abstract fun bindFactory(factory: NotificationsViewModel.Factory): ViewModelAssistedFactory<out ViewModel>

    @Binds
    abstract fun bindSavedStateRegistryOwner(notificationsFragment: NotificationsFragment): SavedStateRegistryOwner

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideDefaultArgs(): Bundle? {
            return null
        }
    }
}