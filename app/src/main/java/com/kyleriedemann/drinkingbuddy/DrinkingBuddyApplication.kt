package com.kyleriedemann.drinkingbuddy

import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import com.github.ajalt.timberkt.Timber
import com.kyleriedemann.drinkingbuddy.data.log.RoomTree
import com.kyleriedemann.drinkingbuddy.di.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import javax.inject.Inject


open class DrinkingBuddyApplication : DaggerApplication() {
    @Inject lateinit var roomTree: RoomTree

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        SoLoader.init(this, false)

        if (BuildConfig.DEBUG) debugTrees().forEach { Timber.plant(it) }
        else releaseTrees().forEach { Timber.plant(it) }

        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val client = AndroidFlipperClient.getInstance(this)
            client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
            client.addPlugin(DatabasesFlipperPlugin(this))
            client.addPlugin(SharedPreferencesFlipperPlugin(this))
            client.start()
        }

        if (BuildConfig.DEBUG) logsShortcut()
    }

    private fun logsShortcut() {
        val shortcutManager = getSystemService(ShortcutManager::class.java)

        val shortcut = ShortcutInfo.Builder(this, "id1")
            .setShortLabel("Logs")
            .setLongLabel("View application logs")
            .setIcon(Icon.createWithResource(this, R.drawable.ic_receipt_black_24dp))
            .setIntent(MainActivity.logsIntent(this))
            .build()

        checkNotNull(shortcutManager)
        shortcutManager.dynamicShortcuts = listOf(shortcut)
    }

    private fun debugTrees() = arrayOf(roomTree, Timber.DebugTree())

    private fun releaseTrees() = arrayOf(roomTree)
}
