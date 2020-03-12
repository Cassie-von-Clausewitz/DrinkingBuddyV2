package com.kyleriedemann.drinkingbuddy.di

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.kyleriedemann.drinkingbuddy.ui.notifications.NotificationDetailFragment
import com.kyleriedemann.drinkingbuddy.ui.notifications.NotificationDetailsViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class NotificationDetailModule {
    @ContributesAndroidInjector(modules = [NotificationDetailViewModelModule::class])
    internal abstract fun notificationDetailFragment(): NotificationDetailFragment
}

@Module
abstract class NotificationDetailViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(NotificationDetailsViewModel::class)
    abstract fun bindFactory(factory: NotificationDetailsViewModel.Factory): ViewModelAssistedFactory<out ViewModel>

    @Binds
    abstract fun bindSavedStateRegistryOwner(notificationDetailFragment: NotificationDetailFragment): SavedStateRegistryOwner

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideDefaultArgs(): Bundle? {
            return null
        }
    }
}