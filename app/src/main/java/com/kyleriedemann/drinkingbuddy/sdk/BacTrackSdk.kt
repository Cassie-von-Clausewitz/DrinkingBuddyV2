@file:Suppress("unused")

package com.kyleriedemann.drinkingbuddy.sdk

import BACtrackAPI.API.BACtrackAPI
import BACtrackAPI.API.BACtrackAPICallbacks
import BACtrackAPI.API.BACtrackAPICallbacksFull
import BACtrackAPI.Constants.BACTrackDeviceType
import BACtrackAPI.Constants.BACtrackUnit
import BACtrackAPI.Exceptions.BluetoothLENotSupportedException
import BACtrackAPI.Exceptions.BluetoothNotEnabledException
import BACtrackAPI.Exceptions.LocationServicesNotEnabledException
import android.app.Application
import androidx.lifecycle.*
import com.snakydesign.livedataextensions.map
import com.snakydesign.livedataextensions.merge
import com.snakydesign.livedataextensions.mergeWith
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.Continuation
import kotlin.coroutines.resumeWithException

/**
 * Created by kyle
 *
 * 1/25/20
 */
typealias BacAPIKeyDeclined = (errorMessage: String) -> Unit
typealias BacAPIKeyAuthorized = () -> Unit
typealias BacConnected = (deviceType: BACTrackDeviceType) -> Unit
typealias BacDidConnect = (message: String) -> Unit
typealias BacDisconnected = () -> Unit
typealias BacConnectionTimeout = () -> Unit
typealias BacFoundBreathalyzer = (device: BACtrackAPI.BACtrackDevice) -> Unit
typealias BacCountdown = (count: Int) -> Unit
typealias BacStart = () -> Unit
typealias BacBlow = () -> Unit
typealias BacAnalyzing = () -> Unit
typealias BacResults = (measuredBac: Float) -> Unit
typealias BacFirmwareVersion = (version: String) -> Unit
typealias BacSerial = (serialHex: String) -> Unit
typealias BacUseCount = (useCount: Int) -> Unit
typealias BacBatteryVoltage = (voltage: Float) -> Unit
typealias BacBatteryLevel = (level: Int) -> Unit
typealias BacError = (errorCode: SdkErrors) -> Unit
typealias BacUnits = (unit: BACtrackUnit) -> Unit

@Suppress("MemberVisibilityCanBePrivate")
class BACtrackDefaultCallbacks(
    val apikeyAcceptedCallbacks: MutableList<BacAPIKeyAuthorized> = mutableListOf(),
    val apiKeyDeclinedCallbacks: MutableList<BacAPIKeyDeclined> = mutableListOf(),
    val connectedCallbacks: MutableList<BacConnected> = mutableListOf(),
    val didConnectCallbacks: MutableList<BacDidConnect> = mutableListOf(),
    val disconnectedCallbacks: MutableList<BacDisconnected> = mutableListOf(),
    val connectionTimeoutCallbacks: MutableList<BacConnectionTimeout> = mutableListOf(),
    val foundBreathalyzerCallbacks: MutableList<BacFoundBreathalyzer> = mutableListOf(),
    val countdownCallbacks: MutableList<BacCountdown> = mutableListOf(),
    val startCallbacks: MutableList<BacStart> = mutableListOf(),
    val blowCallbacks: MutableList<BacBlow> = mutableListOf(),
    val analyzingCallbacks: MutableList<BacAnalyzing> = mutableListOf(),
    val resultsCallbacks: MutableList<BacResults> = mutableListOf(),
    val firmwareVersionCallbacks: MutableList<BacFirmwareVersion> = mutableListOf(),
    val serialCallbacks: MutableList<BacSerial> = mutableListOf(),
    val useCountCallbacks: MutableList<BacUseCount> = mutableListOf(),
    val batteryVoltageCallbacks: MutableList<BacBatteryVoltage> = mutableListOf(),
    val batteryLevelCallbacks: MutableList<BacBatteryLevel> = mutableListOf(),
    val errorCallbacks: MutableList<BacError> = mutableListOf(),
    val unitsCallbacks: MutableList<BacUnits> = mutableListOf()
): BACtrackAPICallbacks {
    override fun BACtrackAPIKeyDeclined(errorMessage: String) = apiKeyDeclinedCallbacks.forEach { it(errorMessage) }
    override fun BACtrackAPIKeyAuthorized() = apikeyAcceptedCallbacks.forEach { it.invoke() }
    override fun BACtrackConnected(deviceType: BACTrackDeviceType) = connectedCallbacks.forEach { it(deviceType) }
    override fun BACtrackDidConnect(s: String) = didConnectCallbacks.forEach { it(s) }
    override fun BACtrackDisconnected() = disconnectedCallbacks.forEach { it() }
    override fun BACtrackConnectionTimeout() = connectionTimeoutCallbacks.forEach { it() }
    override fun BACtrackUnits(unit: BACtrackUnit) = unitsCallbacks.forEach { it(unit) }
    override fun BACtrackFoundBreathalyzer(device: BACtrackAPI.BACtrackDevice) = foundBreathalyzerCallbacks.forEach { it(device) }
    override fun BACtrackCountdown(count: Int) = countdownCallbacks.forEach { it(count) }
    override fun BACtrackStart() = startCallbacks.forEach { it() }
    override fun BACtrackBlow() = blowCallbacks.forEach { it() }
    override fun BACtrackAnalyzing() = analyzingCallbacks.forEach { it() }
    override fun BACtrackResults(measuredBac: Float) = resultsCallbacks.forEach { it(measuredBac) }
    override fun BACtrackFirmwareVersion(version: String) = firmwareVersionCallbacks.forEach { it(version) }
    override fun BACtrackSerial(serialHex: String) = serialCallbacks.forEach { it(serialHex) }
    override fun BACtrackUseCount(useCount: Int) = useCountCallbacks.forEach { it(useCount) }
    override fun BACtrackBatteryVoltage(voltage: Float) = batteryVoltageCallbacks.forEach { it(voltage) }
    override fun BACtrackBatteryLevel(level: Int) = batteryLevelCallbacks.forEach { it(level) }
    override fun BACtrackError(errorCode: Int) = errorCallbacks.forEach { it(SdkErrors.fromInt(errorCode)) }

    fun foldInCallbacks(callbacks: BACtrackDefaultCallbacks): BACtrackDefaultCallbacks {
        apikeyAcceptedCallbacks.addAll(callbacks.apikeyAcceptedCallbacks)
        apiKeyDeclinedCallbacks.addAll(callbacks.apiKeyDeclinedCallbacks)
        connectedCallbacks.addAll(callbacks.connectedCallbacks)
        didConnectCallbacks.addAll(callbacks.didConnectCallbacks)
        disconnectedCallbacks.addAll(callbacks.disconnectedCallbacks)
        connectionTimeoutCallbacks.addAll(callbacks.connectionTimeoutCallbacks)
        foundBreathalyzerCallbacks.addAll(callbacks.foundBreathalyzerCallbacks)
        countdownCallbacks.addAll(callbacks.countdownCallbacks)
        startCallbacks.addAll(callbacks.startCallbacks)
        blowCallbacks.addAll(callbacks.blowCallbacks)
        analyzingCallbacks.addAll(callbacks.analyzingCallbacks)
        resultsCallbacks.addAll(callbacks.resultsCallbacks)
        firmwareVersionCallbacks.addAll(callbacks.firmwareVersionCallbacks)
        serialCallbacks.addAll(callbacks.serialCallbacks)
        useCountCallbacks.addAll(callbacks.useCountCallbacks)
        batteryVoltageCallbacks.addAll(callbacks.batteryVoltageCallbacks)
        batteryLevelCallbacks.addAll(callbacks.batteryLevelCallbacks)
        errorCallbacks.addAll(callbacks.errorCallbacks)
        unitsCallbacks.addAll(callbacks.unitsCallbacks)
        return this
    }
}

typealias BacOnStateActive = () -> Unit
typealias BacCalibrationResults = (calibrationResults: ByteArray) -> Unit
typealias BacConnectionError = () -> Unit
typealias BaaBreathalysersNotFound = () -> Unit
typealias BacTransmitPower = (power: Byte) -> Unit
typealias BacProtectionBit = (protectionBit: ByteArray) -> Unit
typealias BacOnStateIdle = () -> Unit

@Suppress("MemberVisibilityCanBePrivate")
class BacTrackFullCallbacks(
    val apikeyAcceptedCallbacks: MutableList<BacAPIKeyAuthorized> = mutableListOf(),
    val apiKeyDeclinedCallbacks: MutableList<BacAPIKeyDeclined> = mutableListOf(),
    val connectedCallbacks: MutableList<BacConnected> = mutableListOf(),
    val didConnectCallbacks: MutableList<BacDidConnect> = mutableListOf(),
    val disconnectedCallbacks: MutableList<BacDisconnected> = mutableListOf(),
    val connectionTimeoutCallbacks: MutableList<BacConnectionTimeout> = mutableListOf(),
    val foundBreathalyzerCallbacks: MutableList<BacFoundBreathalyzer> = mutableListOf(),
    val countdownCallbacks: MutableList<BacCountdown> = mutableListOf(),
    val startCallbacks: MutableList<BacStart> = mutableListOf(),
    val blowCallbacks: MutableList<BacBlow> = mutableListOf(),
    val analyzingCallbacks: MutableList<BacAnalyzing> = mutableListOf(),
    val resultsCallbacks: MutableList<BacResults> = mutableListOf(),
    val firmwareVersionCallbacks: MutableList<BacFirmwareVersion> = mutableListOf(),
    val serialCallbacks: MutableList<BacSerial> = mutableListOf(),
    val useCountCallbacks: MutableList<BacUseCount> = mutableListOf(),
    val batteryVoltageCallbacks: MutableList<BacBatteryVoltage> = mutableListOf(),
    val batteryLevelCallbacks: MutableList<BacBatteryLevel> = mutableListOf(),
    val errorCallbacks: MutableList<BacError> = mutableListOf(),
    val unitsCallbacks: MutableList<BacUnits> = mutableListOf(),
    val onStateActiveCallbacks: MutableList<BacOnStateActive> = mutableListOf(),
    val calibrationResultsCallbacks: MutableList<BacCalibrationResults> = mutableListOf(),
    val connectionErrorCallbacks: MutableList<BacConnectionError> = mutableListOf(),
    val breathalysersNotFoundCallbacks: MutableList<BaaBreathalysersNotFound> = mutableListOf(),
    val transmitPowerCallbacks: MutableList<BacTransmitPower> = mutableListOf(),
    val protectionBitCallbacks: MutableList<BacProtectionBit> = mutableListOf(),
    val onStateIdleCallbacks: MutableList<BacOnStateIdle> = mutableListOf()
) : BACtrackAPICallbacksFull {
    override fun BACtrackAPIKeyDeclined(errorMessage: String) = apiKeyDeclinedCallbacks.forEach { it(errorMessage) }
    override fun BACtrackAPIKeyAuthorized() = apikeyAcceptedCallbacks.forEach { it.invoke() }
    override fun BACtrackConnected(deviceType: BACTrackDeviceType) = connectedCallbacks.forEach { it(deviceType) }
    override fun BACtrackDidConnect(s: String) = didConnectCallbacks.forEach { it(s) }
    override fun BACtrackDisconnected() = disconnectedCallbacks.forEach { it() }
    override fun BACtrackConnectionTimeout() = connectionTimeoutCallbacks.forEach { it() }
    override fun BACtrackUnits(unit: BACtrackUnit) = unitsCallbacks.forEach { it(unit) }
    override fun BACtrackFoundBreathalyzer(device: BACtrackAPI.BACtrackDevice) = foundBreathalyzerCallbacks.forEach { it(device) }
    override fun BACtrackCountdown(count: Int) = countdownCallbacks.forEach { it(count) }
    override fun BACtrackStart() = startCallbacks.forEach { it() }
    override fun BACtrackBlow() = blowCallbacks.forEach { it() }
    override fun BACtrackAnalyzing() = analyzingCallbacks.forEach { it() }
    override fun BACtrackResults(measuredBac: Float) = resultsCallbacks.forEach { it(measuredBac) }
    override fun BACtrackFirmwareVersion(version: String) = firmwareVersionCallbacks.forEach { it(version) }
    override fun BACtrackSerial(serialHex: String) = serialCallbacks.forEach { it(serialHex) }
    override fun BACtrackUseCount(useCount: Int) = useCountCallbacks.forEach { it(useCount) }
    override fun BACtrackBatteryVoltage(voltage: Byte) = batteryVoltageCallbacks.forEach { it(voltage.toFloat()) }
    override fun BACtrackBatteryLevel(level: Int) = batteryLevelCallbacks.forEach { it(level) }
    override fun BACtrackError(errorCode: Int) = errorCallbacks.forEach { it(SdkErrors.fromInt(errorCode)) }
    override fun BACtrackOnStateActive() = onStateActiveCallbacks.forEach { it() }
    override fun BACtrackCalibrationResults(calibrationResults: ByteArray) = calibrationResultsCallbacks.forEach { it(calibrationResults) }
    override fun BACtrackConnectionError() = connectionErrorCallbacks.forEach { it() }
    override fun BACtrackNoBreathalyzersFound() = breathalysersNotFoundCallbacks.forEach { it() }
    override fun BACtrackTransmitPower(power: Byte) = transmitPowerCallbacks.forEach { it(power) }
    override fun BACtrackProtectionBit(protectionBit: ByteArray) = protectionBitCallbacks.forEach { it(protectionBit) }
    override fun BACtrackOnStateIdle() = onStateIdleCallbacks.forEach { it() }

    fun foldInDefaultCallbacks(bacTrackDefaultCallbacks: BACtrackDefaultCallbacks): BacTrackFullCallbacks {
        apikeyAcceptedCallbacks.addAll(bacTrackDefaultCallbacks.apikeyAcceptedCallbacks)
        apiKeyDeclinedCallbacks.addAll(bacTrackDefaultCallbacks.apiKeyDeclinedCallbacks)
        connectedCallbacks.addAll(bacTrackDefaultCallbacks.connectedCallbacks)
        didConnectCallbacks.addAll(bacTrackDefaultCallbacks.didConnectCallbacks)
        disconnectedCallbacks.addAll(bacTrackDefaultCallbacks.disconnectedCallbacks)
        connectionTimeoutCallbacks.addAll(bacTrackDefaultCallbacks.connectionTimeoutCallbacks)
        foundBreathalyzerCallbacks.addAll(bacTrackDefaultCallbacks.foundBreathalyzerCallbacks)
        countdownCallbacks.addAll(bacTrackDefaultCallbacks.countdownCallbacks)
        startCallbacks.addAll(bacTrackDefaultCallbacks.startCallbacks)
        blowCallbacks.addAll(bacTrackDefaultCallbacks.blowCallbacks)
        analyzingCallbacks.addAll(bacTrackDefaultCallbacks.analyzingCallbacks)
        resultsCallbacks.addAll(bacTrackDefaultCallbacks.resultsCallbacks)
        firmwareVersionCallbacks.addAll(bacTrackDefaultCallbacks.firmwareVersionCallbacks)
        serialCallbacks.addAll(bacTrackDefaultCallbacks.serialCallbacks)
        useCountCallbacks.addAll(bacTrackDefaultCallbacks.useCountCallbacks)
        batteryVoltageCallbacks.addAll(bacTrackDefaultCallbacks.batteryVoltageCallbacks)
        batteryLevelCallbacks.addAll(bacTrackDefaultCallbacks.batteryLevelCallbacks)
        errorCallbacks.addAll(bacTrackDefaultCallbacks.errorCallbacks)
        unitsCallbacks.addAll(bacTrackDefaultCallbacks.unitsCallbacks)
        return this
    }

    fun foldInCallbacks(bacTrackFullCallbacks: BacTrackFullCallbacks): BacTrackFullCallbacks {
        apikeyAcceptedCallbacks.addAll(bacTrackFullCallbacks.apikeyAcceptedCallbacks)
        apiKeyDeclinedCallbacks.addAll(bacTrackFullCallbacks.apiKeyDeclinedCallbacks)
        connectedCallbacks.addAll(bacTrackFullCallbacks.connectedCallbacks)
        didConnectCallbacks.addAll(bacTrackFullCallbacks.didConnectCallbacks)
        disconnectedCallbacks.addAll(bacTrackFullCallbacks.disconnectedCallbacks)
        connectionTimeoutCallbacks.addAll(bacTrackFullCallbacks.connectionTimeoutCallbacks)
        foundBreathalyzerCallbacks.addAll(bacTrackFullCallbacks.foundBreathalyzerCallbacks)
        countdownCallbacks.addAll(bacTrackFullCallbacks.countdownCallbacks)
        startCallbacks.addAll(bacTrackFullCallbacks.startCallbacks)
        blowCallbacks.addAll(bacTrackFullCallbacks.blowCallbacks)
        analyzingCallbacks.addAll(bacTrackFullCallbacks.analyzingCallbacks)
        resultsCallbacks.addAll(bacTrackFullCallbacks.resultsCallbacks)
        firmwareVersionCallbacks.addAll(bacTrackFullCallbacks.firmwareVersionCallbacks)
        serialCallbacks.addAll(bacTrackFullCallbacks.serialCallbacks)
        useCountCallbacks.addAll(bacTrackFullCallbacks.useCountCallbacks)
        batteryVoltageCallbacks.addAll(bacTrackFullCallbacks.batteryVoltageCallbacks)
        batteryLevelCallbacks.addAll(bacTrackFullCallbacks.batteryLevelCallbacks)
        errorCallbacks.addAll(bacTrackFullCallbacks.errorCallbacks)
        unitsCallbacks.addAll(bacTrackFullCallbacks.unitsCallbacks)
        onStateActiveCallbacks.addAll(bacTrackFullCallbacks.onStateActiveCallbacks)
        calibrationResultsCallbacks.addAll(bacTrackFullCallbacks.calibrationResultsCallbacks)
        connectionErrorCallbacks.addAll(bacTrackFullCallbacks.connectionErrorCallbacks)
        breathalysersNotFoundCallbacks.addAll(bacTrackFullCallbacks.breathalysersNotFoundCallbacks)
        transmitPowerCallbacks.addAll(bacTrackFullCallbacks.transmitPowerCallbacks)
        protectionBitCallbacks.addAll(bacTrackFullCallbacks.protectionBitCallbacks)
        onStateIdleCallbacks.addAll(bacTrackFullCallbacks.onStateIdleCallbacks)
        return this
    }
}

interface ListCallbacks {
    val apikeyAcceptedCallbacks: MutableList<BacAPIKeyAuthorized>
    val apiKeyDeclinedCallbacks: MutableList<BacAPIKeyDeclined>
    val connectedCallbacks: MutableList<BacConnected>
    val didConnectCallbacks: MutableList<BacDidConnect>
    val disconnectedCallbacks: MutableList<BacDisconnected>
    val connectionTimeoutCallbacks: MutableList<BacConnectionTimeout>
    val foundBreathalyzerCallbacks: MutableList<BacFoundBreathalyzer>
    val countdownCallbacks: MutableList<BacCountdown>
    val startCallbacks: MutableList<BacStart>
    val blowCallbacks: MutableList<BacBlow>
    val analyzingCallbacks: MutableList<BacAnalyzing>
    val resultsCallbacks: MutableList<BacResults>
    val firmwareVersionCallbacks: MutableList<BacFirmwareVersion>
    val serialCallbacks: MutableList<BacSerial>
    val useCountCallbacks: MutableList<BacUseCount>
    val batteryVoltageCallbacks: MutableList<BacBatteryVoltage>
    val batteryLevelCallbacks: MutableList<BacBatteryLevel>
    val errorCallbacks: MutableList<BacError>
    val unitsCallbacks: MutableList<BacUnits>
    val onStateActiveCallbacks: MutableList<BacOnStateActive>
    val calibrationResultsCallbacks: MutableList<BacCalibrationResults>
    val connectionErrorCallbacks: MutableList<BacConnectionError>
    val breathalysersNotFoundCallbacks: MutableList<BaaBreathalysersNotFound>
    val transmitPowerCallbacks: MutableList<BacTransmitPower>
    val protectionBitCallbacks: MutableList<BacProtectionBit>
    val onStateIdleCallbacks: MutableList<BacOnStateIdle>
}

sealed class ApiKeyEvents {
    object Accepted: ApiKeyEvents()
    class Declined(val message: String): ApiKeyEvents()
}

sealed class ConnectedEvents {
    class FoundDevice(val device: Device): ConnectedEvents()
    class Connected(val deviceType: DeviceType): ConnectedEvents()
    class DidConnect(val message: String): ConnectedEvents()
    object Disconnected: ConnectedEvents()
    object Timeout: ConnectedEvents()
    object NoDevicesFound: ConnectedEvents()
    object ConnectionError: ConnectedEvents()
}

sealed class ReadingEvents {
    object Start: ReadingEvents()
    class Countdown(val count: Int): ReadingEvents()
    object Blow: ReadingEvents()
    object Analyzing: ReadingEvents()
    class Result(val reading: Float): ReadingEvents()
}

sealed class DeviceInformationEvents {
    class Firmware(val firmware: String): DeviceInformationEvents()
    class Serial(val serial: String): DeviceInformationEvents()
    class UseCount(val uses: Int): DeviceInformationEvents()
    class BatteryVoltage(val voltage: Float): DeviceInformationEvents()
    class BatteryLevel(val level: Int): DeviceInformationEvents()
    class Units(val units: BACtrackUnit): DeviceInformationEvents()
    class CalibrationResults(val calibrationResults: ByteArray): DeviceInformationEvents()
    class TransmitPower(val transmitPower: Byte): DeviceInformationEvents()
    class ProtectionBit(val protectionBit: ByteArray): DeviceInformationEvents()
}

sealed class StateEvents {
    object Active: StateEvents()
    object Idle: StateEvents()
}

class LiveDataCallbacks: BACtrackAPICallbacksFull {
    //<editor-fold desc="Errors Events">
    private val _errorLiveData = MutableLiveData<Int>()
    val errorLiveData: LiveData<SdkErrors> = _errorLiveData.map {
        SdkErrors.fromInt(it)
    }
    override fun BACtrackError(errorCode: Int) = _errorLiveData.postValue(errorCode)
    //</editor-fold>

    //<editor-fold desc="Api Key Events">
    private val _apikeyAcceptedLiveData = MutableLiveData<Unit>()
    val apiKeyAcceptedLiveData: LiveData<ApiKeyEvents> = _apikeyAcceptedLiveData.map {
        ApiKeyEvents.Accepted
    }
    override fun BACtrackAPIKeyDeclined(errorMessage: String) = _apiKeyDeclinedLiveData.postValue(errorMessage)

    private val _apiKeyDeclinedLiveData = MutableLiveData<String>()
    val apiKeyDeclinedLiveData: LiveData<ApiKeyEvents> = _apiKeyDeclinedLiveData.map {
        ApiKeyEvents.Declined(it)
    }
    override fun BACtrackAPIKeyAuthorized() = _apikeyAcceptedLiveData.postValue(Unit)

    val apiKeyLiveData = apiKeyAcceptedLiveData.mergeWith(apiKeyDeclinedLiveData)
    //</editor-fold>

    //<editor-fold desc="Connected Events">
    private val _foundBreathalyzerLiveData = MutableLiveData<Device>()
    val foundBreathalyzerLiveData: LiveData<ConnectedEvents> = _foundBreathalyzerLiveData.map {
        ConnectedEvents.FoundDevice(it)
    }
    override fun BACtrackFoundBreathalyzer(device: BACtrackAPI.BACtrackDevice) = _foundBreathalyzerLiveData.postValue(device)

    private val _connectedLiveData = MutableLiveData<DeviceType>()
    val connectedLiveData: LiveData<ConnectedEvents> = _connectedLiveData.map {
        ConnectedEvents.Connected(it)
    }
    override fun BACtrackConnected(deviceType: BACTrackDeviceType) = _connectedLiveData.postValue(deviceType)

    private val _didConnectLiveData = MutableLiveData<String>()
    val didConnectLiveData: LiveData<ConnectedEvents> = _didConnectLiveData.map {
        ConnectedEvents.DidConnect(it)
    }
    override fun BACtrackDidConnect(message: String) = _didConnectLiveData.postValue(message)

    private val _disconnectedLiveData = MutableLiveData<Unit>()
    val disconnectedLiveData: LiveData<ConnectedEvents> = _disconnectedLiveData.map {
        ConnectedEvents.Disconnected
    }
    override fun BACtrackDisconnected() = _disconnectedLiveData.postValue(Unit)

    private val _connectionTimeoutLiveData = MutableLiveData<Unit>()
    val connectionTimeoutLiveData: LiveData<ConnectedEvents> = _connectionTimeoutLiveData.map {
        ConnectedEvents.Timeout
    }
    override fun BACtrackConnectionTimeout() = _connectionTimeoutLiveData.postValue(Unit)

    private val _breathalysersNotFoundLiveData = MutableLiveData<Unit>()
    val breathalysersNotFoundLiveData: LiveData<ConnectedEvents> = _breathalysersNotFoundLiveData.map {
        ConnectedEvents.NoDevicesFound
    }
    override fun BACtrackNoBreathalyzersFound() = _breathalysersNotFoundLiveData.postValue(Unit)

    private val _connectionErrorLiveData = MutableLiveData<Unit>()
    val connectionErrorLiveData: LiveData<ConnectedEvents> = _connectionErrorLiveData.map {
        ConnectedEvents.ConnectionError
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
    val startLiveData: LiveData<ReadingEvents> = _startLiveData.map {
        ReadingEvents.Start
    }
    override fun BACtrackStart() = _startLiveData.postValue(Unit)

    private val _countdownLiveData = MutableLiveData<Int>()
    val countdownLiveData: LiveData<ReadingEvents> = _countdownLiveData.map {
        ReadingEvents.Countdown(it)
    }
    override fun BACtrackCountdown(count: Int) = _countdownLiveData.postValue(count)

    private val _blowLiveData = MutableLiveData<Unit>()
    val blowLiveData: LiveData<ReadingEvents> = _blowLiveData.map {
        ReadingEvents.Blow
    }
    override fun BACtrackBlow() = _blowLiveData.postValue(Unit)

    private val _analyzingLiveData = MutableLiveData<Unit>()
    val analyzingLiveData: LiveData<ReadingEvents> = _analyzingLiveData.map {
        ReadingEvents.Analyzing
    }
    override fun BACtrackAnalyzing() = _analyzingLiveData.postValue(Unit)

    private val _resultsLiveData = MutableLiveData<Float>()
    val resultsLiveData: LiveData<ReadingEvents> = _resultsLiveData.map {
        ReadingEvents.Result(it)
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
    val firmwareVersionLiveData: LiveData<DeviceInformationEvents> = _firmwareVersionLiveData.map {
        DeviceInformationEvents.Firmware(it)
    }
    override fun BACtrackFirmwareVersion(version: String) = _firmwareVersionLiveData.postValue(version)

    private val _serialLiveData = MutableLiveData<String>()
    val serialLiveData: LiveData<DeviceInformationEvents> = _serialLiveData.map {
        DeviceInformationEvents.Serial(it)
    }
    override fun BACtrackSerial(serialHex: String) = _serialLiveData.postValue(serialHex)

    private val _useCountLiveData = MutableLiveData<Int>()
    val useCountLiveData: LiveData<DeviceInformationEvents> = _useCountLiveData.map {
        DeviceInformationEvents.UseCount(it)
    }
    override fun BACtrackUseCount(useCount: Int) = _useCountLiveData.postValue(useCount)

    private val _batteryVoltageLiveData = MutableLiveData<Float>()
    val batteryVoltageLiveData: LiveData<DeviceInformationEvents> = _batteryVoltageLiveData.map {
        DeviceInformationEvents.BatteryVoltage(it)
    }
    override fun BACtrackBatteryVoltage(voltage: Byte) = _batteryVoltageLiveData.postValue(voltage.toFloat())

    private val _batteryLevelLiveData = MutableLiveData<Int>()
    val batteryLevelLiveData: LiveData<DeviceInformationEvents> = _batteryLevelLiveData.map {
        DeviceInformationEvents.BatteryLevel(it)
    }
    override fun BACtrackBatteryLevel(level: Int) = _batteryLevelLiveData.postValue(level)

    private val _unitsLiveData = MutableLiveData<BACtrackUnit>()
    val unitsLiveData: LiveData<DeviceInformationEvents> = _unitsLiveData.map {
        DeviceInformationEvents.Units(it)
    }
    override fun BACtrackUnits(unit: BACtrackUnit) = _unitsLiveData.postValue(unit)

    private val _calibrationResultsLiveData = MutableLiveData<ByteArray>()
    val calibrationResultsLiveData: LiveData<DeviceInformationEvents> = _calibrationResultsLiveData.map {
        DeviceInformationEvents.CalibrationResults(it)
    }
    override fun BACtrackCalibrationResults(calibrationResults: ByteArray) = _calibrationResultsLiveData.postValue(calibrationResults)

    private val _transmitPowerLiveData = MutableLiveData<Byte>()
    val transmitPowerLiveData: LiveData<DeviceInformationEvents> = _transmitPowerLiveData.map {
        DeviceInformationEvents.TransmitPower(it)
    }
    override fun BACtrackTransmitPower(power: Byte) = _transmitPowerLiveData.postValue(power)

    private val _protectionBitLiveData = MutableLiveData<ByteArray>()
    val protectionBitLiveData: LiveData<DeviceInformationEvents> = _protectionBitLiveData.map {
        DeviceInformationEvents.ProtectionBit(it)
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
    val stateActiveLiveData: LiveData<StateEvents> = _stateActiveLiveData.map {
        StateEvents.Active
    }
    override fun BACtrackOnStateActive() = _stateActiveLiveData.postValue(Unit)

    private val _stateIdleLiveData = MutableLiveData<Unit>()
    val stateIdleLiveData: LiveData<StateEvents> = _stateIdleLiveData.map {
        StateEvents.Idle
    }
    override fun BACtrackOnStateIdle() = _stateIdleLiveData.postValue(Unit)

    val stateEvents = stateActiveLiveData.mergeWith(stateIdleLiveData)
    //</editor-fold>
}

// todo use this as a template to convert this to flows
class FlowCallbacks: BACtrackAPICallbacksFull {
    //<editor-fold desc="Errors Events">
    private val _errorLiveData = MutableLiveData<Int>()
    val errorLiveData: LiveData<SdkErrors> = _errorLiveData.map {
        SdkErrors.fromInt(it)
    }
    override fun BACtrackError(errorCode: Int) = _errorLiveData.postValue(errorCode)
    //</editor-fold>

    //<editor-fold desc="Api Key Events">
    private val _apikeyAcceptedLiveData = MutableLiveData<Unit>()
    val apiKeyAcceptedLiveData: LiveData<ApiKeyEvents> = _apikeyAcceptedLiveData.map {
        ApiKeyEvents.Accepted
    }
    override fun BACtrackAPIKeyDeclined(errorMessage: String) = _apiKeyDeclinedLiveData.postValue(errorMessage)

    private val _apiKeyDeclinedLiveData = MutableLiveData<String>()
    val apiKeyDeclinedLiveData: LiveData<ApiKeyEvents> = _apiKeyDeclinedLiveData.map {
        ApiKeyEvents.Declined(it)
    }
    override fun BACtrackAPIKeyAuthorized() = _apikeyAcceptedLiveData.postValue(Unit)

    val apiKeyLiveData = apiKeyAcceptedLiveData.mergeWith(apiKeyDeclinedLiveData)
    //</editor-fold>

    //<editor-fold desc="Connected Events">
    private val _foundBreathalyzerLiveData = MutableLiveData<Device>()
    val foundBreathalyzerLiveData: LiveData<ConnectedEvents> = _foundBreathalyzerLiveData.map {
        ConnectedEvents.FoundDevice(it)
    }
    override fun BACtrackFoundBreathalyzer(device: BACtrackAPI.BACtrackDevice) = _foundBreathalyzerLiveData.postValue(device)

    private val _connectedLiveData = MutableLiveData<DeviceType>()
    val connectedLiveData: LiveData<ConnectedEvents> = _connectedLiveData.map {
        ConnectedEvents.Connected(it)
    }
    override fun BACtrackConnected(deviceType: BACTrackDeviceType) = _connectedLiveData.postValue(deviceType)

    private val _didConnectLiveData = MutableLiveData<String>()
    val didConnectLiveData: LiveData<ConnectedEvents> = _didConnectLiveData.map {
        ConnectedEvents.DidConnect(it)
    }
    override fun BACtrackDidConnect(message: String) = _didConnectLiveData.postValue(message)

    private val _disconnectedLiveData = MutableLiveData<Unit>()
    val disconnectedLiveData: LiveData<ConnectedEvents> = _disconnectedLiveData.map {
        ConnectedEvents.Disconnected
    }
    override fun BACtrackDisconnected() = _disconnectedLiveData.postValue(Unit)

    private val _connectionTimeoutLiveData = MutableLiveData<Unit>()
    val connectionTimeoutLiveData: LiveData<ConnectedEvents> = _connectionTimeoutLiveData.map {
        ConnectedEvents.Timeout
    }
    override fun BACtrackConnectionTimeout() = _connectionTimeoutLiveData.postValue(Unit)

    private val _breathalysersNotFoundLiveData = MutableLiveData<Unit>()
    val breathalysersNotFoundLiveData: LiveData<ConnectedEvents> = _breathalysersNotFoundLiveData.map {
        ConnectedEvents.NoDevicesFound
    }
    override fun BACtrackNoBreathalyzersFound() = _breathalysersNotFoundLiveData.postValue(Unit)

    private val _connectionErrorLiveData = MutableLiveData<Unit>()
    val connectionErrorLiveData: LiveData<ConnectedEvents> = _connectionErrorLiveData.map {
        ConnectedEvents.ConnectionError
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
    val startLiveData: LiveData<ReadingEvents> = _startLiveData.map {
        ReadingEvents.Start
    }
    override fun BACtrackStart() = _startLiveData.postValue(Unit)

    private val _countdownLiveData = MutableLiveData<Int>()
    val countdownLiveData: LiveData<ReadingEvents> = _countdownLiveData.map {
        ReadingEvents.Countdown(it)
    }
    override fun BACtrackCountdown(count: Int) = _countdownLiveData.postValue(count)

    private val _blowLiveData = MutableLiveData<Unit>()
    val blowLiveData: LiveData<ReadingEvents> = _blowLiveData.map {
        ReadingEvents.Blow
    }
    override fun BACtrackBlow() = _blowLiveData.postValue(Unit)

    private val _analyzingLiveData = MutableLiveData<Unit>()
    val analyzingLiveData: LiveData<ReadingEvents> = _analyzingLiveData.map {
        ReadingEvents.Analyzing
    }
    override fun BACtrackAnalyzing() = _analyzingLiveData.postValue(Unit)

    private val _resultsLiveData = MutableLiveData<Float>()
    val resultsLiveData: LiveData<ReadingEvents> = _resultsLiveData.map {
        ReadingEvents.Result(it)
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
    val firmwareVersionLiveData: LiveData<DeviceInformationEvents> = _firmwareVersionLiveData.map {
        DeviceInformationEvents.Firmware(it)
    }
    override fun BACtrackFirmwareVersion(version: String) = _firmwareVersionLiveData.postValue(version)

    private val _serialLiveData = MutableLiveData<String>()
    val serialLiveData: LiveData<DeviceInformationEvents> = _serialLiveData.map {
        DeviceInformationEvents.Serial(it)
    }
    override fun BACtrackSerial(serialHex: String) = _serialLiveData.postValue(serialHex)

    private val _useCountLiveData = MutableLiveData<Int>()
    val useCountLiveData: LiveData<DeviceInformationEvents> = _useCountLiveData.map {
        DeviceInformationEvents.UseCount(it)
    }
    override fun BACtrackUseCount(useCount: Int) = _useCountLiveData.postValue(useCount)

    private val _batteryVoltageLiveData = MutableLiveData<Float>()
    val batteryVoltageLiveData: LiveData<DeviceInformationEvents> = _batteryVoltageLiveData.map {
        DeviceInformationEvents.BatteryVoltage(it)
    }
    override fun BACtrackBatteryVoltage(voltage: Byte) = _batteryVoltageLiveData.postValue(voltage.toFloat())

    private val _batteryLevelLiveData = MutableLiveData<Int>()
    val batteryLevelLiveData: LiveData<DeviceInformationEvents> = _batteryLevelLiveData.map {
        DeviceInformationEvents.BatteryLevel(it)
    }
    override fun BACtrackBatteryLevel(level: Int) = _batteryLevelLiveData.postValue(level)

    private val _unitsLiveData = MutableLiveData<BACtrackUnit>()
    val unitsLiveData: LiveData<DeviceInformationEvents> = _unitsLiveData.map {
        DeviceInformationEvents.Units(it)
    }
    override fun BACtrackUnits(unit: BACtrackUnit) = _unitsLiveData.postValue(unit)

    private val _calibrationResultsLiveData = MutableLiveData<ByteArray>()
    val calibrationResultsLiveData: LiveData<DeviceInformationEvents> = _calibrationResultsLiveData.map {
        DeviceInformationEvents.CalibrationResults(it)
    }
    override fun BACtrackCalibrationResults(calibrationResults: ByteArray) = _calibrationResultsLiveData.postValue(calibrationResults)

    private val _transmitPowerLiveData = MutableLiveData<Byte>()
    val transmitPowerLiveData: LiveData<DeviceInformationEvents> = _transmitPowerLiveData.map {
        DeviceInformationEvents.TransmitPower(it)
    }
    override fun BACtrackTransmitPower(power: Byte) = _transmitPowerLiveData.postValue(power)

    private val _protectionBitLiveData = MutableLiveData<ByteArray>()
    val protectionBitLiveData: LiveData<DeviceInformationEvents> = _protectionBitLiveData.map {
        DeviceInformationEvents.ProtectionBit(it)
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
    val stateActiveLiveData: LiveData<StateEvents> = _stateActiveLiveData.map {
        StateEvents.Active
    }
    override fun BACtrackOnStateActive() = _stateActiveLiveData.postValue(Unit)

    private val _stateIdleLiveData = MutableLiveData<Unit>()
    val stateIdleLiveData: LiveData<StateEvents> = _stateIdleLiveData.map {
        StateEvents.Idle
    }
    override fun BACtrackOnStateIdle() = _stateIdleLiveData.postValue(Unit)

    val stateEvents = stateActiveLiveData.mergeWith(stateIdleLiveData)
    //</editor-fold>
}

typealias DeviceType = BACTrackDeviceType
typealias Device = BACtrackAPI.BACtrackDevice

class BacTrackSdk(
    private val application: Application,
    private val apiKey: String,
    callbacks: BACtrackDefaultCallbacks = BACtrackDefaultCallbacks(),
    private val fullCallbacks: BacTrackFullCallbacks = BacTrackFullCallbacks(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    init {
        fullCallbacks.foldInDefaultCallbacks(callbacks)
    }

    private var _bacTrackSdk: BACtrackAPI? = null

    private val liveDataCallbacks = LiveDataCallbacks()

    val errorEvents = liveDataCallbacks.errorLiveData
    val apiKeyEvents = liveDataCallbacks.apiKeyLiveData
    val connectedEvents = liveDataCallbacks.connectedEvents
    val readingEvents = liveDataCallbacks.readingEvents
    val deviceInformationEvents = liveDataCallbacks.deviceInformationEvents
    val stateEvents = liveDataCallbacks.stateEvents

    fun start() {
        try {
            _bacTrackSdk = BACtrackAPI(application, liveDataCallbacks, apiKey)
            _bacTrackSdk?.breathalyzerBatteryVoltage
        } catch (t: BluetoothLENotSupportedException) {
            Timber.e("BLE not supported")
        } catch (t: LocationServicesNotEnabledException) {
            Timber.e("Location services not enabled")
        } catch (t: BluetoothNotEnabledException) {
            Timber.e("Bluetooth not enabled")
        }
    }

    val isConnected = _bacTrackSdk?.isConnected ?: false

    fun connectToClosestDevice() {
        if (_bacTrackSdk == null) throw SdkNotInitialized()
        _bacTrackSdk?.connectToNearestBreathalyzer()
    }

    fun takeReading() = throwIfNotInitialized {
        _bacTrackSdk?.startCountdown()
    }

    suspend fun connectToClosestDeviceAsync() = withContext(ioDispatcher) {
        suspendCoroutine<DeviceType> {
            it.errorIfSdkNotInitialized()

            fullCallbacks.connectedCallbacks.add { deviceType: DeviceType ->
                it.resume(deviceType)
            }
            fullCallbacks.connectionErrorCallbacks.add {
                it.resumeWithConnectionError()
            }

            _bacTrackSdk?.connectToNearestBreathalyzer()
        }
    }

    suspend fun getSerialNumberAsync() = withContext(ioDispatcher) {
        suspendCoroutine<String> {
            it.errorIfSdkNotInitialized()

            fullCallbacks.serialCallbacks.add { serial ->
                it.resume(serial)
            }

            fullCallbacks.connectionErrorCallbacks.add {
                it.resumeWithConnectionError()
            }

            fullCallbacks.disconnectedCallbacks.add {
                it.resumeWithConnectionError()
            }

            _bacTrackSdk?.serialNumber
        }
    }

    suspend fun getFirmwareVersionAsync() = withContext(ioDispatcher) {
        suspendCoroutine<String> {
            it.errorIfSdkNotInitialized()

            fullCallbacks.firmwareVersionCallbacks.add { firmware ->
                it.resume(firmware)
            }

            fullCallbacks.connectionErrorCallbacks.add {
                it.resumeWithConnectionError()
            }

            fullCallbacks.disconnectedCallbacks.add {
                it.resumeWithConnectionError()
            }

            _bacTrackSdk?.firmwareVersion
        }
    }

    suspend fun readingFlow() = flow {
        fullCallbacks.countdownCallbacks.add {
            runBlocking {
                emit("Countdown: $it")
            }
        }

        fullCallbacks.analyzingCallbacks.add {
            runBlocking {
                emit("Analyzing")
            }
        }

        fullCallbacks.resultsCallbacks.add {
            runBlocking {
                emit("Reading: $it")
            }
        }

        _bacTrackSdk?.startCountdown()
    }.flowOn(ioDispatcher)

    private fun <T> Continuation<T>.errorIfSdkNotInitialized() {
        if (_bacTrackSdk == null) this.resumeWithException(SdkNotInitialized())
    }

    private fun <T> Continuation<T>.resumeWithConnectionError() {
        this.resumeWithException(SdkConnectionError())
    }

    private fun BacTrackSdk.throwIfNotInitialized(block: () -> Unit) {
        if (this._bacTrackSdk == null) throw SdkNotInitialized()
        block.invoke()
    }
}

class SdkNotInitialized: Exception("SDK was not initialized before use")
class SdkConnectionError: Exception("SDK connection error")

enum class SdkErrors(val errorCode: Byte) {
    TIME_OUT(1),
    BLOW_ERROR(2),
    OUT_OF_TEMPERATURE(3),
    LOW_BATTERY(4),
    CALIBRATION_FAIL(5),
    NOT_CALIBRATED(6),
    COM_ERROR(7),
    INFLOW_ERROR(8),
    SOLENOID_ERROR(9),
    MAX_BAC_EXCEEDED_ERROR(10),
    UNKNOWN_ERROR(11);

    companion object {
        fun fromByte(errorCode: Byte) = fromInt(errorCode.toInt())

        fun fromInt(errorCode: Int): SdkErrors {
            return when(errorCode) {
                1 -> TIME_OUT
                2 -> BLOW_ERROR
                3 -> OUT_OF_TEMPERATURE
                4 -> LOW_BATTERY
                5 -> CALIBRATION_FAIL
                6 -> NOT_CALIBRATED
                7 -> COM_ERROR
                8 -> INFLOW_ERROR
                9 -> SOLENOID_ERROR
                10 -> MAX_BAC_EXCEEDED_ERROR
                11 -> UNKNOWN_ERROR
                else -> UNKNOWN_ERROR
            }
        }
    }
}

fun BACtrackAPI.BACtrackDevice.readableToString(): String = "BACtrackDevice(device: ${this.device}, rssi: ${this.rssi}, services: ${this.services}, type: ${this.type})"
