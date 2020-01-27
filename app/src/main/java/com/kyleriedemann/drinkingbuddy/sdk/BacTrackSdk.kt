package com.kyleriedemann.drinkingbuddy.sdk

import BACtrackAPI.API.BACtrackAPI
import BACtrackAPI.API.BACtrackAPICallbacks
import BACtrackAPI.Constants.BACTrackDeviceType
import BACtrackAPI.Constants.BACtrackUnit

/**
 * Created by kyle
 *
 * 1/25/20
 */
typealias bacAPIKeyDeclined = (errorMessage: String) -> Unit
typealias bacAPIKeyAuthorized = () -> Unit
typealias bacConnected = (deviceType: BACTrackDeviceType) -> Unit
typealias bacDidConnect = (message: String) -> Unit
typealias bacDisconnected = () -> Unit
typealias bacConnectionTimeout = () -> Unit
typealias bacFoundBreathalyzer = (device: BACtrackAPI.BACtrackDevice) -> Unit
typealias bacCountdown = (count: Int) -> Unit
typealias bacStart = () -> Unit
typealias bacBlow = () -> Unit
typealias bacAnalyzing = () -> Unit
typealias bacResults = (measuredBac: Float) -> Unit
typealias bacFirmwareVersion = (version: String) -> Unit
typealias bacSerial = (serialHex: String) -> Unit
typealias bacUseCount = (useCount: Int) -> Unit
typealias bacBatteryVoltage = (voltage: Float) -> Unit
typealias bacBatteryLevel = (level: Int) -> Unit
typealias bacError = (errorCode: SdkErrors) -> Unit
typealias bacUnits = (unit: BACtrackUnit) -> Unit

@Suppress("MemberVisibilityCanBePrivate", "unused")
class BACtrackDefaultCallbacks(
        val apikeyAcceptedCallbacks: MutableList<bacAPIKeyAuthorized> = mutableListOf(),
        val apiKeyDeclinedCallbacks: MutableList<bacAPIKeyDeclined> = mutableListOf(),
        val connectedCallbacks: MutableList<bacConnected> = mutableListOf(),
        val didConnectCallbacks: MutableList<bacDidConnect> = mutableListOf(),
        val disconnectedCallbacks: MutableList<bacDisconnected> = mutableListOf(),
        val connectionTimeoutCallbacks: MutableList<bacConnectionTimeout> = mutableListOf(),
        val foundBreathalyzerCallbacks: MutableList<bacFoundBreathalyzer> = mutableListOf(),
        val countdownCallbacks: MutableList<bacCountdown> = mutableListOf(),
        val startCallbacks: MutableList<bacStart> = mutableListOf(),
        val blowCallbacks: MutableList<bacBlow> = mutableListOf(),
        val analyzingCallbacks: MutableList<bacAnalyzing> = mutableListOf(),
        val resultsCallbacks: MutableList<bacResults> = mutableListOf(),
        val firmwareVersionCallbacks: MutableList<bacFirmwareVersion> = mutableListOf(),
        val serialCallbacks: MutableList<bacSerial> = mutableListOf(),
        val useCountCallbacks: MutableList<bacUseCount> = mutableListOf(),
        val batteryVoltageCallbacks: MutableList<bacBatteryVoltage> = mutableListOf(),
        val batteryLevelCallbacks: MutableList<bacBatteryLevel> = mutableListOf(),
        val errorCallbacks: MutableList<bacError> = mutableListOf(),
        val unitsCallbacks: MutableList<bacUnits> = mutableListOf()
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
}

@Suppress("unused")
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

@Suppress("unused")
fun BACtrackAPI.BACtrackDevice.readableToString(): String = "BACtrackDevice(device: ${this.device}, rssi: ${this.rssi}, services: ${this.services}, type: ${this.type})"
