package com.kyleriedemann.drinkingbuddy.di

import android.app.Application
import com.kyleriedemann.drinkingbuddy.BuildConfig
import com.kyleriedemann.drinkingbuddy.sdk.BACtrackDefaultCallbacks
import com.kyleriedemann.drinkingbuddy.sdk.BacTrackFullCallbacks
import com.kyleriedemann.drinkingbuddy.sdk.BacTrackSdk
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule {
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