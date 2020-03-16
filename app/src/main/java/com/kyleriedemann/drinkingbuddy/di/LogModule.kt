package com.kyleriedemann.drinkingbuddy.di

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.kyleriedemann.drinkingbuddy.data.DrinkingBuddyDb
import com.kyleriedemann.drinkingbuddy.data.log.RoomTree
import com.kyleriedemann.drinkingbuddy.data.source.local.LogDao
import com.kyleriedemann.drinkingbuddy.ui.log.LogListFragment
import com.kyleriedemann.drinkingbuddy.ui.log.LogListViewModel
import com.kyleriedemann.drinkingbuddy.ui.notifications.NotificationsFragment
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
object LogModule {
    @JvmStatic @Singleton @Provides
    fun provideLogDao(database: DrinkingBuddyDb): LogDao = database.logDao()

    @JvmStatic
    @Singleton
    @Provides
    fun provideRoomTimberTree(logDao: LogDao, dispatcher: CoroutineDispatcher): RoomTree {
        return RoomTree(logDao, dispatcher)
    }
}

@Module
abstract class LogListModule {
    @ContributesAndroidInjector(modules = [LogListViewModelModule::class])
    internal abstract fun logListFragment(): LogListFragment
}

@Module
abstract class LogListViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(LogListViewModel::class)
    abstract fun bindFactory(factory: LogListViewModel.Factory): ViewModelAssistedFactory<out ViewModel>

    @Binds
    abstract fun bindSavedStateRegistryOwner(logListFragment: LogListFragment): SavedStateRegistryOwner

    @Module
    companion object {
        @JvmStatic
        @Provides
        fun provideDefaultArgs(): Bundle? {
            return null
        }
    }
}