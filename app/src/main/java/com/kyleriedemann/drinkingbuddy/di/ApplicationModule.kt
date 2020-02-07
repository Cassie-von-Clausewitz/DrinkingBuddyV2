package com.kyleriedemann.drinkingbuddy.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.kyleriedemann.drinkingbuddy.BuildConfig
import com.kyleriedemann.drinkingbuddy.data.DrinkingBuddyDb
import com.kyleriedemann.drinkingbuddy.data.source.*
import com.kyleriedemann.drinkingbuddy.data.source.local.NotificationLocalDataSource
import com.kyleriedemann.drinkingbuddy.data.source.local.ReadingLocalDataSource
import com.kyleriedemann.drinkingbuddy.sdk.BACtrackDefaultCallbacks
import com.kyleriedemann.drinkingbuddy.sdk.BacTrackFullCallbacks
import com.kyleriedemann.drinkingbuddy.sdk.BacTrackSdk
import dagger.Binds
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module(includes = [ApplicationModuleBinds::class])
object ApplicationModule {
    @JvmStatic
    @Singleton
    @Provides
    fun provideIoDispatcher() = Dispatchers.IO

    @JvmStatic
    @Singleton
    @Provides
    fun providesDatabase(application: Application): DrinkingBuddyDb {
        return Room.databaseBuilder(
            application,
            DrinkingBuddyDb::class.java,
            "DrinkingBuddy.db"
        ).build()
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideNotificationLocalDataSource(
        database: DrinkingBuddyDb,
        ioDispatcher: CoroutineDispatcher
    ): NotificationDataSource {
        return NotificationLocalDataSource(
            database.notificationDao(),
            ioDispatcher
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun provideReadingLocalDataSource(
        database: DrinkingBuddyDb,
        ioDispatcher: CoroutineDispatcher
    ): ReadingDataSource {
        return ReadingLocalDataSource(
            database.readingDao(),
            ioDispatcher
        )
    }

    @JvmStatic
    @Singleton
    @Provides
    fun providesBacTrackSdk(
        application: Application,
        defaultCallbacks: Set<BACtrackDefaultCallbacks>,
        fullCallbacks: Set<BacTrackFullCallbacks>
    ): BacTrackSdk {
        return BacTrackSdk(
            application,
            BuildConfig.BAC_TRACK_TOKEN,
            defaultCallbacks.fold(BACtrackDefaultCallbacks(), { acc, next -> acc.foldInCallbacks(next) }),
            fullCallbacks.fold(BacTrackFullCallbacks(), { acc, next -> acc.foldInCallbacks(next) })
        )
    }
}

@Module
abstract class ApplicationModuleBinds {
    @Binds
    @Singleton
    abstract fun bindNotificationRepository(repo: DefaultNotificationRepository): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindReadingRepository(repo: DefaultReadingDataRepository): ReadingRepository
}