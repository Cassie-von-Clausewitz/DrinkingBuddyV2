package com.kyleriedemann.drinkingbuddy

import android.util.Log
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.soloader.SoLoader
import com.kyleriedemann.drinkingbuddy.data.log.RoomTree
import com.kyleriedemann.drinkingbuddy.data.source.local.LogDao
import com.kyleriedemann.drinkingbuddy.di.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber
import timber.log.Timber.DebugTree
import javax.inject.Inject


open class DrinkingBuddyApplication : DaggerApplication() {
    @Inject lateinit var roomTree: RoomTree

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        SoLoader.init(this, false)

        if (BuildConfig.DEBUG) Timber.plant(*debugTrees())
        else Timber.plant(*releaseTrees())
        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val client = AndroidFlipperClient.getInstance(this)
            client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
            client.addPlugin(DatabasesFlipperPlugin(this))
            client.start()
        }
    }

    private fun debugTrees() = arrayOf(roomTree, DebugTree())

    private fun releaseTrees() = arrayOf(roomTree)
}
