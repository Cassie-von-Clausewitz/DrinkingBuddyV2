package com.kyleriedemann.drinkingbuddy.sdk

import BACtrackAPI.API.BACtrackAPI
import BACtrackAPI.API.BACtrackAPICallbacksFull
import BACtrackAPI.Constants.BACTrackDeviceType
import BACtrackAPI.Constants.BACtrackUnit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.snakydesign.livedataextensions.map
import com.snakydesign.livedataextensions.merge
import com.snakydesign.livedataextensions.mergeWith
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import timber.log.Timber

class LiveDataCallbacks: BACtrackAPICallbacksFull {
    //<editor-fold desc="Errors Events">
    private val _errorLiveData = MutableLiveData<Int>()
    val errorEvents: LiveData<SdkEvent.ErrorEvent> = _errorLiveData.map {
        SdkEvent.ErrorEvent(SdkError.fromInt(it))
    }
    override fun BACtrackError(errorCode: Int) = _errorLiveData.postValue(errorCode)
    //</editor-fold>

    //<editor-fold desc="Api Key Events">
    private val _apikeyAcceptedLiveData = MutableLiveData<Unit>()
    val apiKeyAcceptedLiveData: LiveData<SdkEvent.ApiKeyEvent> = _apikeyAcceptedLiveData.map {
        SdkEvent.ApiKeyEvent.Accepted
    }
    override fun BACtrackAPIKeyDeclined(errorMessage: String) = _apiKeyDeclinedLiveData.postValue(errorMessage)

    private val _apiKeyDeclinedLiveData = MutableLiveData<String>()
    val apiKeyDeclinedLiveData: LiveData<SdkEvent.ApiKeyEvent> = _apiKeyDeclinedLiveData.map {
        SdkEvent.ApiKeyEvent.Declined(it)
    }
    override fun BACtrackAPIKeyAuthorized() = _apikeyAcceptedLiveData.postValue(Unit)

    val apiKeyEvents = apiKeyAcceptedLiveData.mergeWith(apiKeyDeclinedLiveData)
    //</editor-fold>

    //<editor-fold desc="Connected Events">
    private val _foundBreathalyzerLiveData = MutableLiveData<Device>()
    val foundBreathalyzerLiveData: LiveData<SdkEvent.ConnectedEvent> = _foundBreathalyzerLiveData.map {
        SdkEvent.ConnectedEvent.FoundDevice(it)
    }
    override fun BACtrackFoundBreathalyzer(device: BACtrackAPI.BACtrackDevice) = _foundBreathalyzerLiveData.postValue(device)

    private val _connectedLiveData = MutableLiveData<DeviceType>()
    val connectedLiveData: LiveData<SdkEvent.ConnectedEvent> = _connectedLiveData.map {
        SdkEvent.ConnectedEvent.Connected(it)
    }
    override fun BACtrackConnected(deviceType: BACTrackDeviceType) = _connectedLiveData.postValue(deviceType)

    private val _didConnectLiveData = MutableLiveData<String>()
    val didConnectLiveData: LiveData<SdkEvent.ConnectedEvent> = _didConnectLiveData.map {
        SdkEvent.ConnectedEvent.DidConnect(it)
    }
    override fun BACtrackDidConnect(message: String) = _didConnectLiveData.postValue(message)

    private val _disconnectedLiveData = MutableLiveData<Unit>()
    val disconnectedLiveData: LiveData<SdkEvent.ConnectedEvent> = _disconnectedLiveData.map {
        SdkEvent.ConnectedEvent.Disconnected
    }
    override fun BACtrackDisconnected() = _disconnectedLiveData.postValue(Unit)

    private val _connectionTimeoutLiveData = MutableLiveData<Unit>()
    val connectionTimeoutLiveData: LiveData<SdkEvent.ConnectedEvent> = _connectionTimeoutLiveData.map {
        SdkEvent.ConnectedEvent.Timeout
    }
    override fun BACtrackConnectionTimeout() = _connectionTimeoutLiveData.postValue(Unit)

    private val _breathalysersNotFoundLiveData = MutableLiveData<Unit>()
    val breathalysersNotFoundLiveData: LiveData<SdkEvent.ConnectedEvent> = _breathalysersNotFoundLiveData.map {
        SdkEvent.ConnectedEvent.NoDevicesFound
    }
    override fun BACtrackNoBreathalyzersFound() = _breathalysersNotFoundLiveData.postValue(Unit)

    private val _connectionErrorLiveData = MutableLiveData<Unit>()
    val connectionErrorLiveData: LiveData<SdkEvent.ConnectedEvent> = _connectionErrorLiveData.map {
        SdkEvent.ConnectedEvent.ConnectionError
    }
    override fun BACtrackConnectionError() = _connectionErrorLiveData.postValue(Unit)

    val connectedEvents = merge(listOf(
        foundBreathalyzerLiveData,
        connectedLiveData,
        didConnectLiveData,
        disconnectedLiveData,
        connectionTimeoutLiveData,
        breathalysersNotFoundLiveData,
        connectionErrorLiveData
    ))
    //</editor-fold>

    //<editor-fold desc="Reading Events">
    private val _startLiveData = MutableLiveData<Unit>()
    val startLiveData: LiveData<SdkEvent.ReadingEvent> = _startLiveData.map {
        SdkEvent.ReadingEvent.Start
    }
    override fun BACtrackStart() = _startLiveData.postValue(Unit)

    private val _countdownLiveData = MutableLiveData<Int>()
    val countdownLiveData: LiveData<SdkEvent.ReadingEvent> = _countdownLiveData.map {
        SdkEvent.ReadingEvent.Countdown(it)
    }
    override fun BACtrackCountdown(count: Int) = _countdownLiveData.postValue(count)

    private val _blowLiveData = MutableLiveData<Unit>()
    val blowLiveData: LiveData<SdkEvent.ReadingEvent> = _blowLiveData.map {
        SdkEvent.ReadingEvent.Blow
    }
    override fun BACtrackBlow() = _blowLiveData.postValue(Unit)

    private val _analyzingLiveData = MutableLiveData<Unit>()
    val analyzingLiveData: LiveData<SdkEvent.ReadingEvent> = _analyzingLiveData.map {
        SdkEvent.ReadingEvent.Analyzing
    }
    override fun BACtrackAnalyzing() = _analyzingLiveData.postValue(Unit)

    private val _resultsLiveData = MutableLiveData<Float>()
    val resultsLiveData: LiveData<SdkEvent.ReadingEvent> = _resultsLiveData.map {
        SdkEvent.ReadingEvent.Result(it)
    }
    override fun BACtrackResults(measuredBac: Float) = _resultsLiveData.postValue(measuredBac)

    val readingEvents = merge(listOf(
        startLiveData,
        countdownLiveData,
        blowLiveData,
        analyzingLiveData,
        resultsLiveData
    ))
    //</editor-fold>

    //<editor-fold desc="Device Information Events">
    private val _firmwareVersionLiveData = MutableLiveData<String>()
    val firmwareVersionLiveData: LiveData<SdkEvent.DeviceInformationEvent> = _firmwareVersionLiveData.map {
        SdkEvent.DeviceInformationEvent.Firmware(it)
    }
    override fun BACtrackFirmwareVersion(version: String) = _firmwareVersionLiveData.postValue(version)

    private val _serialLiveData = MutableLiveData<String>()
    val serialLiveData: LiveData<SdkEvent.DeviceInformationEvent> = _serialLiveData.map {
        SdkEvent.DeviceInformationEvent.Serial(it)
    }
    override fun BACtrackSerial(serialHex: String) = _serialLiveData.postValue(serialHex)

    private val _useCountLiveData = MutableLiveData<Int>()
    val useCountLiveData: LiveData<SdkEvent.DeviceInformationEvent> = _useCountLiveData.map {
        SdkEvent.DeviceInformationEvent.UseCount(it)
    }
    override fun BACtrackUseCount(useCount: Int) = _useCountLiveData.postValue(useCount)

    private val _batteryVoltageLiveData = MutableLiveData<Float>()
    val batteryVoltageLiveData: LiveData<SdkEvent.DeviceInformationEvent> = _batteryVoltageLiveData.map {
        SdkEvent.DeviceInformationEvent.BatteryVoltage(it)
    }
    override fun BACtrackBatteryVoltage(voltage: Byte) = _batteryVoltageLiveData.postValue(voltage.toFloat())

    private val _batteryLevelLiveData = MutableLiveData<Int>()
    val batteryLevelLiveData: LiveData<SdkEvent.DeviceInformationEvent> = _batteryLevelLiveData.map {
        SdkEvent.DeviceInformationEvent.BatteryLevel(it)
    }
    override fun BACtrackBatteryLevel(level: Int) = _batteryLevelLiveData.postValue(level)

    private val _unitsLiveData = MutableLiveData<BACtrackUnit>()
    val unitsLiveData: LiveData<SdkEvent.DeviceInformationEvent> = _unitsLiveData.map {
        SdkEvent.DeviceInformationEvent.Units(it)
    }
    override fun BACtrackUnits(unit: BACtrackUnit) = _unitsLiveData.postValue(unit)

    private val _calibrationResultsLiveData = MutableLiveData<ByteArray>()
    val calibrationResultsLiveData: LiveData<SdkEvent.DeviceInformationEvent> = _calibrationResultsLiveData.map {
        SdkEvent.DeviceInformationEvent.CalibrationResults(it)
    }
    override fun BACtrackCalibrationResults(calibrationResults: ByteArray) = _calibrationResultsLiveData.postValue(calibrationResults)

    private val _transmitPowerLiveData = MutableLiveData<Byte>()
    val transmitPowerLiveData: LiveData<SdkEvent.DeviceInformationEvent> = _transmitPowerLiveData.map {
        SdkEvent.DeviceInformationEvent.TransmitPower(it)
    }
    override fun BACtrackTransmitPower(power: Byte) = _transmitPowerLiveData.postValue(power)

    private val _protectionBitLiveData = MutableLiveData<ByteArray>()
    val protectionBitLiveData: LiveData<SdkEvent.DeviceInformationEvent> = _protectionBitLiveData.map {
        SdkEvent.DeviceInformationEvent.ProtectionBit(it)
    }
    override fun BACtrackProtectionBit(protectionBit: ByteArray) = _protectionBitLiveData.postValue(protectionBit)

    val deviceInformationEvents = merge(listOf(
        firmwareVersionLiveData,
        serialLiveData,
        useCountLiveData,
        batteryVoltageLiveData,
        batteryLevelLiveData,
        unitsLiveData,
        calibrationResultsLiveData,
        transmitPowerLiveData,
        protectionBitLiveData
    ))
    //</editor-fold>

    //<editor-fold desc="State Events">
    private val _stateActiveLiveData = MutableLiveData<Unit>()
    val stateActiveLiveData: LiveData<SdkEvent.StateEvent> = _stateActiveLiveData.map {
        SdkEvent.StateEvent.Active
    }
    override fun BACtrackOnStateActive() = _stateActiveLiveData.postValue(Unit)

    private val _stateIdleLiveData = MutableLiveData<Unit>()
    val stateIdleLiveData: LiveData<SdkEvent.StateEvent> = _stateIdleLiveData.map {
        SdkEvent.StateEvent.Idle
    }
    override fun BACtrackOnStateIdle() = _stateIdleLiveData.postValue(Unit)

    val stateEvents = stateActiveLiveData.mergeWith(stateIdleLiveData)
    //</editor-fold>
}

class ChannelCallbacks(private val dispatcher: CoroutineDispatcher): BACtrackAPICallbacksFull {
    //<editor-fold desc="Errors Events">
    val errorEvents = ConflatedBroadcastChannel<SdkEvent.ErrorEvent>()
    override fun BACtrackError(errorCode: Int) = errorEvents.postValue(SdkEvent.ErrorEvent(SdkError.fromInt(errorCode)))
    //</editor-fold>

    //<editor-fold desc="Api Key Events">
    val apiKeyEvents = ConflatedBroadcastChannel<SdkEvent.ApiKeyEvent>()

    override fun BACtrackAPIKeyAuthorized() = apiKeyEvents.postValue(SdkEvent.ApiKeyEvent.Accepted)
    override fun BACtrackAPIKeyDeclined(errorMessage: String) = apiKeyEvents.postValue(SdkEvent.ApiKeyEvent.Declined(errorMessage))
    //</editor-fold>

    //<editor-fold desc="Connected Events">
    val connectedEvents = ConflatedBroadcastChannel<SdkEvent.ConnectedEvent>()

    override fun BACtrackFoundBreathalyzer(device: BACtrackAPI.BACtrackDevice) = connectedEvents.postValue(SdkEvent.ConnectedEvent.FoundDevice(device))
    override fun BACtrackConnected(deviceType: BACTrackDeviceType) = connectedEvents.postValue(SdkEvent.ConnectedEvent.Connected(deviceType))
    override fun BACtrackDidConnect(message: String) = connectedEvents.postValue(SdkEvent.ConnectedEvent.DidConnect(message))
    override fun BACtrackDisconnected() = connectedEvents.postValue(SdkEvent.ConnectedEvent.Disconnected)
    override fun BACtrackConnectionTimeout() = connectedEvents.postValue(SdkEvent.ConnectedEvent.Timeout)
    override fun BACtrackNoBreathalyzersFound() = connectedEvents.postValue(SdkEvent.ConnectedEvent.NoDevicesFound)
    override fun BACtrackConnectionError() = connectedEvents.postValue(SdkEvent.ConnectedEvent.ConnectionError)
    //</editor-fold>

    //<editor-fold desc="Reading Events">
    val readingEvents = ConflatedBroadcastChannel<SdkEvent.ReadingEvent>()

    override fun BACtrackStart() = readingEvents.postValue(SdkEvent.ReadingEvent.Start)
    override fun BACtrackCountdown(count: Int) = readingEvents.postValue(SdkEvent.ReadingEvent.Countdown(count))
    override fun BACtrackBlow() = readingEvents.postValue(SdkEvent.ReadingEvent.Blow)
    override fun BACtrackAnalyzing() = readingEvents.postValue(SdkEvent.ReadingEvent.Analyzing)
    override fun BACtrackResults(measuredBac: Float) = readingEvents.postValue(SdkEvent.ReadingEvent.Result(measuredBac))
    //</editor-fold>

    //<editor-fold desc="Device Information Events">
    val deviceInformationEvents = ConflatedBroadcastChannel<SdkEvent.DeviceInformationEvent>()

    override fun BACtrackFirmwareVersion(version: String) = deviceInformationEvents.postValue(SdkEvent.DeviceInformationEvent.Firmware(version))
    override fun BACtrackSerial(serialHex: String) = deviceInformationEvents.postValue(SdkEvent.DeviceInformationEvent.Serial(serialHex))
    override fun BACtrackUseCount(useCount: Int) = deviceInformationEvents.postValue(SdkEvent.DeviceInformationEvent.UseCount(useCount))
    override fun BACtrackBatteryVoltage(voltage: Byte) = deviceInformationEvents.postValue(SdkEvent.DeviceInformationEvent.BatteryVoltage(voltage.toFloat()))
    override fun BACtrackBatteryLevel(level: Int) = deviceInformationEvents.postValue(SdkEvent.DeviceInformationEvent.BatteryLevel(level))
    override fun BACtrackUnits(unit: BACtrackUnit) = deviceInformationEvents.postValue(SdkEvent.DeviceInformationEvent.Units(unit))
    override fun BACtrackCalibrationResults(calibrationResults: ByteArray) = deviceInformationEvents.postValue(SdkEvent.DeviceInformationEvent.CalibrationResults(calibrationResults))
    override fun BACtrackTransmitPower(power: Byte) = deviceInformationEvents.postValue(SdkEvent.DeviceInformationEvent.TransmitPower(power))
    override fun BACtrackProtectionBit(protectionBit: ByteArray) = deviceInformationEvents.postValue(SdkEvent.DeviceInformationEvent.ProtectionBit(protectionBit))
    //</editor-fold>

    //<editor-fold desc="State Events">
    val stateEvents = ConflatedBroadcastChannel<SdkEvent.StateEvent>()

    override fun BACtrackOnStateActive() = stateEvents.postValue(SdkEvent.StateEvent.Active)
    override fun BACtrackOnStateIdle() = stateEvents.postValue(SdkEvent.StateEvent.Idle)
    //</editor-fold>

    val allEvents = Channel<SdkEvent>(1000)

    /**
     * Wrapper extension to make the conversion back and forth with [LiveData] easier
     *
     * Also allows us to set a method to posting the value directly like we could with live data
     */
    private fun <E: SdkEvent> ConflatedBroadcastChannel<E>.postValue(element: E) {
        Timber.tag("SdkEvent").d("$element")
        Dispatchers.IO.dispatch(GlobalScope.coroutineContext, Runnable {
            this.offer(element)
            allEvents.offer(element)
        })
    }
}

