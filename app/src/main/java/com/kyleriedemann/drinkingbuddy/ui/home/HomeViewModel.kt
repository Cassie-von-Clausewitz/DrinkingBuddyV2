package com.kyleriedemann.drinkingbuddy.ui.home

import androidx.lifecycle.*
import com.kyleriedemann.drinkingbuddy.data.models.Notification
import com.kyleriedemann.drinkingbuddy.data.models.Reading
import com.kyleriedemann.drinkingbuddy.data.source.NotificationRepository
import com.kyleriedemann.drinkingbuddy.data.source.ReadingRepository
import com.kyleriedemann.drinkingbuddy.di.ViewModelAssistedFactory
import com.kyleriedemann.drinkingbuddy.sdk.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class HomeViewModel @AssistedInject constructor (
    @Assisted private val handle: SavedStateHandle,
    private val coroutineSdk: CoroutineBacTrackSdk,
    private val readingRepository: ReadingRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val dispatcher = Dispatchers.IO

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    var prediction: Float = 0.0f

    fun showPrediction() = _text.postValue("Prediction: $prediction")

//    val connected = sdk.connectedEvents.map {
//        when (it) {
//            is ConnectedEvents.Connected -> saveDeviceConnected(it.deviceType)
//            is ConnectedEvents.FoundDevice -> saveDeviceFound(it.device)
//            is ConnectedEvents.DidConnect -> saveDidConnect(it.message)
//        }
//        it
//    }
//    val error = sdk.errorEvents
//    val reading = sdk.readingEvents.map {
//        if (it is ReadingEvents.Result) {
//            saveReading(it)
//        }
//        it
//    }

    private val _connected = MutableLiveData<ConnectedEvents>()
    val connected: LiveData<ConnectedEvents> = _connected

    private val _error = MutableLiveData<SdkErrors>()
    val error: LiveData<SdkErrors> = _error

    private val _reading = MutableLiveData<ReadingEvents>()
    val reading: LiveData<ReadingEvents> = _reading

    private fun saveReading(readingResult: ReadingEvents.Result) = viewModelScope.launch(dispatcher) {
        readingRepository.insertReading(Reading(result = readingResult.reading, prediction = prediction))
    }

    private fun saveDeviceConnected(deviceType: DeviceType) = viewModelScope.launch(dispatcher) {
        notificationRepository.insertNotification(Notification("Device Connected", "$deviceType"))
    }

    private fun saveDeviceFound(device: Device) = viewModelScope.launch(dispatcher) {
        notificationRepository.insertNotification(Notification("Device Found", "$device"))
    }

    private fun saveDidConnect(message: String) = viewModelScope.launch(dispatcher) {
        notificationRepository.insertNotification(Notification("Device Did Connect", message))
    }

    fun permissionsGranted() {
//        sdk.start()
        Timber.i("Starting SDK")
        coroutineSdk.start()
        receiveApiEvents()
        receiveConnectedEvents()
        receiveReadingEvents()
        receiveDeviceInfoEvents()
        receiveErrorEvents()
    }

    private fun receiveApiEvents() = viewModelScope.launch(dispatcher) {
        Timber.i("Opening channel subscriptions")
        coroutineSdk.apiKeyEvents.openSubscription().consumeAsFlow().distinctUntilChanged().collect {
            Timber.i("ApiKeyEvent: $it")
        }
    }

    private fun receiveConnectedEvents() = viewModelScope.launch(dispatcher) {
        coroutineSdk.connectedEvents.openSubscription().consumeAsFlow().distinctUntilChanged().collect {
            Timber.i("ConnectedEvent: $it")
            when(it) {
                is ConnectedEvents.Connected -> saveDeviceConnected(it.deviceType)
                is ConnectedEvents.FoundDevice -> saveDeviceFound(it.device)
                is ConnectedEvents.DidConnect -> saveDidConnect(it.message)
            }
            _connected.postValue(it)
        }
    }

    private fun countdown() = viewModelScope.launch(dispatcher) {

    }

    private fun receiveReadingEvents() = viewModelScope.launch(dispatcher) {
        coroutineSdk.readingEvents.openSubscription().consumeAsFlow().distinctUntilChanged().collect {
            Timber.i("ReadingEvent: $it")
            if (it is ReadingEvents.Result) {
                saveReading(it)
            }
            _reading.postValue(it)
        }
    }

    private fun receiveDeviceInfoEvents() = viewModelScope.launch(dispatcher) {
        coroutineSdk.deviceInformationEvents.openSubscription().consumeAsFlow().distinctUntilChanged().collect {
            Timber.i("DeviceInformationEvent: $it")
            notificationRepository.insertNotification(Notification("Device Information", it.toString()))
        }
    }

    private fun receiveErrorEvents() = viewModelScope.launch(dispatcher) {
        coroutineSdk.errorEvents.openSubscription().consumeAsFlow().flowOn(dispatcher).collect {
            Timber.i("ErrorEvent: $it")
            _error.postValue(it)
        }
    }

    fun connectToClosestDevice() {
        _text.postValue("Connecting to device")
        coroutineSdk.connectToClosestDevice()
    }

    fun takeReading() {
        _text.postValue("Taking reading")
        coroutineSdk.takeReading()
    }

    fun connectToClosestDeviceAsync() = viewModelScope.launch {
        try {
            _text.postValue("Connecting to device...")
            val deviceType = coroutineSdk.connectToClosestDeviceAsync()
            _text.postValue("Connected to device type $deviceType")
            notificationRepository.insertNotification(Notification("Device Connected", "$deviceType"))
        } catch (e: Exception) {
            displayException(e)
        }
    }

    fun getDeviceFirmware() = viewModelScope.launch {
        try {
            _text.postValue("Reading firmware...")
            val firmwareVersion = coroutineSdk.getFirmwareVersionAsync()
            _text.postValue("Firmware: $firmwareVersion")
            notificationRepository.insertNotification(Notification("Firmware Version", firmwareVersion))
        } catch (e: Exception) {
            displayException(e)
        }
    }

    fun getSerialNumber() = viewModelScope.launch {
        try {
            _text.postValue("Reading serial number...")
            val serialNumber = coroutineSdk.getSerialNumberAsync()
            _text.postValue("Serial: $serialNumber")
            notificationRepository.insertNotification(Notification("Serial Number", serialNumber))
        } catch (e: Exception) {
            displayException(e)
        }
    }

    fun takeReadingAsync() = viewModelScope.launch {
        coroutineSdk.readingFlow().collect {
            _text.postValue(it)
            if (it.contains("Result")) {
                return@collect
            }
        }
    }

    private fun displayException(e: Exception) {
        _text.postValue(e.message)
    }

    fun sendWelcomeNotification() = viewModelScope.launch {
        notificationRepository.insertNotification(Notification(title = "Welcome!", message = "Connect a device to start taking readings."))
    }

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<HomeViewModel>
}
