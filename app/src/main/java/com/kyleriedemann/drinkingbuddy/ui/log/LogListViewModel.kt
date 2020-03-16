package com.kyleriedemann.drinkingbuddy.ui.log

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.kyleriedemann.drinkingbuddy.data.source.local.LogDao
import com.kyleriedemann.drinkingbuddy.di.ViewModelAssistedFactory
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

class LogListViewModel @AssistedInject constructor(
    @Assisted private val handle: SavedStateHandle,
    private val logDao: LogDao
): ViewModel() {
    val items = logDao.getLogs()

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<LogListViewModel>
}