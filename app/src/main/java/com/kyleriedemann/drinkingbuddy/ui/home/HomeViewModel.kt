package com.kyleriedemann.drinkingbuddy.ui.home

import androidx.lifecycle.*
import com.kyleriedemann.drinkingbuddy.data.Result
import com.kyleriedemann.drinkingbuddy.data.models.Notification
import com.kyleriedemann.drinkingbuddy.data.source.NotificationRepository
import com.kyleriedemann.drinkingbuddy.data.source.ReadingRepository
import com.kyleriedemann.drinkingbuddy.di.ViewModelAssistedFactory
import com.kyleriedemann.drinkingbuddy.sdk.BacTrackSdk
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.flow.collect
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

    fun connectToClosestDeviceAsync() = viewModelScope.launch {
        try {
            val deviceType = sdk.connectToClosestDeviceAsync()
            _text.postValue("Connected to device type $deviceType")
            notificationRepository.insertNotification(Notification("Device Connected", "$deviceType"))
        } catch (e: Exception) {
            _text.postValue(e.message)
        }
    }

    fun getDeviceFirmware() = viewModelScope.launch {
        val firmwareVersion = sdk.getFirmwareVersionAsync()
        notificationRepository.insertNotification(Notification("Firmware Version", firmwareVersion))
    }

    fun getSerialNumber() = viewModelScope.launch {
        val serialNumber = sdk.getSerialNumberAsync()
        notificationRepository.insertNotification(Notification("Serial Number", serialNumber))
    }

    fun takeReading() = viewModelScope.launch {
        sdk.readingFlow().collect {
            _text.postValue(it)
            if (it.contains("Result")) {
                return@collect
            }
        }
    }

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<HomeViewModel>
}
