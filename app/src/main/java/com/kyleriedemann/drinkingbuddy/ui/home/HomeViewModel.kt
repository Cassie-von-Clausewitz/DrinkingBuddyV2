package com.kyleriedemann.drinkingbuddy.ui.home

import androidx.lifecycle.*
import com.kyleriedemann.drinkingbuddy.data.Result
import com.kyleriedemann.drinkingbuddy.data.source.NotificationRepository
import com.kyleriedemann.drinkingbuddy.data.source.ReadingRepository
import com.kyleriedemann.drinkingbuddy.di.ViewModelAssistedFactory
import com.kyleriedemann.drinkingbuddy.sdk.BacTrackSdk
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch

class HomeViewModel @AssistedInject constructor (
    @Assisted private val handle: SavedStateHandle,
    private val sdk: BacTrackSdk,
    private val readingRepository: ReadingRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    fun permissionsGranted() {
        sdk.start()
    }

    fun connectToClosestDevice() = viewModelScope.launch {
        val result = sdk.connectToClosestDeviceAsync()
        when (result) {
            is Result.Success -> _text.postValue("Connected!")
            is Result.Error -> _text.postValue(result.exception.message)
            Result.Loading -> _text.postValue("Connecting")
        }
    }

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<HomeViewModel>
}
