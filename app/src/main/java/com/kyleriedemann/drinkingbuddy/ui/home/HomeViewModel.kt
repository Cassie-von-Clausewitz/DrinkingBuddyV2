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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel @AssistedInject constructor (
    @Assisted private val handle: SavedStateHandle,
    private val sdk: BacTrackSdk,
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

    private val _connected = MutableLiveData<SdkEvent.ConnectedEvent>()
    val connected: LiveData<SdkEvent.ConnectedEvent> = _connected

    private val _error = MutableLiveData<SdkEvent.ErrorEvent>()
    val error: LiveData<SdkEvent.ErrorEvent> = _error

    private val _reading = MutableLiveData<SdkEvent.ReadingEvent>()
    val reading: LiveData<SdkEvent.ReadingEvent> = _reading

    private fun saveReading(readingResult: SdkEvent.ReadingEvent.Result) = viewModelScope.launch(dispatcher) {
        readingRepository.insertReading(Reading(result = readingResult.reading, prediction = prediction))
    }

    private fun saveDeviceConnected(deviceType: DeviceType) = viewModelScope.launch(dispatcher) {
        notificationRepository.insertNotification(Notification("Device Connected", "$deviceType"))
    }

    private fun saveDeviceFound(device: Device) = viewModelScope.launch(dispatcher) {
        notificationRepository.insertNotification(Notification("Device Found", device.readableToString()))
    }

    private fun saveDidConnect(message: String) = viewModelScope.launch(dispatcher) {
        notificationRepository.insertNotification(Notification("Device Did Connect", message))
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    fun permissionsGranted() {
        Timber.i("Starting SDK")
        sdk.start()
        receiveApiEvents()
        receiveConnectedEvents()
        receiveReadingEvents()
        receiveDeviceInfoEvents()
        receiveErrorEvents()
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun receiveApiEvents() = viewModelScope.launch(dispatcher) {
        Timber.i("Opening channel subscriptions")
        sdk.apiKeyEvents.openSubscription().consumeAsFlow().distinctUntilChanged().collect {
            Timber.i("ApiKeyEvent: $it")
        }
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun receiveConnectedEvents() = viewModelScope.launch(dispatcher) {
        sdk.connectedEvents.openSubscription().consumeAsFlow().distinctUntilChanged().collect {
            Timber.i("ConnectedEvent: $it")
            when(it) {
                is SdkEvent.ConnectedEvent.Connected -> saveDeviceConnected(it.deviceType)
                is SdkEvent.ConnectedEvent.FoundDevice -> saveDeviceFound(it.device)
                is SdkEvent.ConnectedEvent.DidConnect -> saveDidConnect(it.message)
            }
            _connected.postValue(it)
        }
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun receiveReadingEvents() = viewModelScope.launch(dispatcher) {
        sdk.readingEvents.openSubscription().consumeAsFlow().distinctUntilChanged().collect {
            Timber.i("ReadingEvent: $it")
            if (it is SdkEvent.ReadingEvent.Result) {
                saveReading(it)
            }
            _reading.postValue(it)
        }
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun receiveDeviceInfoEvents() = viewModelScope.launch(dispatcher) {
        sdk.deviceInformationEvents.openSubscription().consumeAsFlow().distinctUntilChanged().collect {
            Timber.i("DeviceInformationEvent: $it")
            notificationRepository.insertNotification(Notification("Device Information", it.toString()))
        }
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun receiveErrorEvents() = viewModelScope.launch(dispatcher) {
        sdk.errorEvents.openSubscription().consumeAsFlow().flowOn(dispatcher).collect {
            Timber.i("ErrorEvent: $it")
            _error.postValue(it)
        }
    }

    fun connectToClosestDevice() {
        _text.postValue("Connecting to device")
        sdk.safeCall { it.connectToClosestDevice() }
    }

    fun disconnect() {
        sdk.safeCall { it.disconnect() }
    }

    fun takeReading() {
        _text.postValue("Taking reading")
        sdk.safeCall { it.takeReading() }
    }

    fun getDeviceFirmware() = viewModelScope.launch {
        _text.postValue("Reading firmware...")
        sdk.safeCall { it.getFirmwareVersion() }
    }

    fun getSerialNumber() = viewModelScope.launch {
        _text.postValue("Reading serial number...")
        sdk.safeCall { it.getSerialNumber() }
    }

    fun sendWelcomeNotification() = viewModelScope.launch {
        _text.postValue("Sent welcome notification!")
        notificationRepository.insertNotification(Notification(title = "Welcome!", message = "Connect a device to start taking readings."))
    }

    private fun BacTrackSdk.safeCall(block: (BacTrackSdk) -> Unit) {
        try {
            block.invoke(this)
        } catch (t: SdkNotInitialized) {
            Timber.e(t, "Do you have bluetooth enabled?")
            _text.postValue("Do you have bluetooth enabled?")
        }
    }

    @AssistedInject.Factory
    interface Factory : ViewModelAssistedFactory<HomeViewModel>
}
