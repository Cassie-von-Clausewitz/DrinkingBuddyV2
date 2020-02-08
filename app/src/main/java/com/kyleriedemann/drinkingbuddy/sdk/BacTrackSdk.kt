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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.kyleriedemann.drinkingbuddy.data.Result
import com.kyleriedemann.drinkingbuddy.data.Result.Success
import com.kyleriedemann.drinkingbuddy.data.Result.Error
import kotlinx.coroutines.channels.produce
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

typealias DeviceType = BACTrackDeviceType

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

    private var bacTrackSdk: BACtrackAPI? = null

    fun start() {
        try {
            bacTrackSdk = BACtrackAPI(application, fullCallbacks, apiKey)
            bacTrackSdk?.breathalyzerBatteryVoltage
        } catch (t: BluetoothLENotSupportedException) {
            Timber.e("BLE not supported")
        } catch (t: LocationServicesNotEnabledException) {
            Timber.e("Location services not enabled")
        } catch (t: BluetoothNotEnabledException) {
            Timber.e("Bluetooth not enabled")
        }
    }

    val isConnected = bacTrackSdk?.isConnected ?: false

    suspend fun connectToClosestDeviceAsync() = withContext(ioDispatcher) {
        suspendCoroutine<DeviceType> {
            it.errorIfSdkNotInitialized()

            fullCallbacks.connectedCallbacks.add { deviceType: DeviceType ->
                it.resume(deviceType)
            }
            fullCallbacks.connectionErrorCallbacks.add {
                it.resumeWithConnectionError()
            }

            bacTrackSdk?.connectToNearestBreathalyzer()
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

            bacTrackSdk?.serialNumber
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

            bacTrackSdk?.firmwareVersion
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

        bacTrackSdk?.startCountdown()
    }.flowOn(ioDispatcher)

    private fun <T> Continuation<T>.errorIfSdkNotInitialized() {
        if (bacTrackSdk == null) this.resumeWithException(SdkNotInitialized())
    }

    private fun <T> Continuation<T>.resumeWithConnectionError() {
        this.resumeWithException(SdkConnectionError())
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
