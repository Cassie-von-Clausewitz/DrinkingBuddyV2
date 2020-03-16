package com.kyleriedemann.drinkingbuddy.di

import com.kyleriedemann.drinkingbuddy.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector
    internal abstract fun mainActivity(): MainActivity
}