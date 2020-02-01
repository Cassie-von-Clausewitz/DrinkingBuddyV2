package com.kyleriedemann.drinkingbuddy.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.kyleriedemann.drinkingbuddy.di.ViewModelAssistedFactory
import com.kyleriedemann.drinkingbuddy.sdk.BacTrackSdk
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject

class HomeViewModel @AssistedInject constructor (
    @Assisted private val handle: SavedStateHandle,
    private val sdk: BacTrackSdk
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    fun permissionsGranted() {
        sdk.start()
    }

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<HomeViewModel>
}
