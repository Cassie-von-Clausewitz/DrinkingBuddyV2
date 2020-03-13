package com.kyleriedemann.drinkingbuddy.di

import com.kyleriedemann.drinkingbuddy.data.DrinkingBuddyDb
import com.kyleriedemann.drinkingbuddy.data.log.RoomTree
import com.kyleriedemann.drinkingbuddy.data.source.local.LogDao
import dagger.Module
import dagger.Provides
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