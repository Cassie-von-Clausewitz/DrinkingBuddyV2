package com.kyleriedemann.drinkingbuddy.sdk

import BACtrackAPI.API.BACtrackAPI
import BACtrackAPI.Constants.BACTrackDeviceType
import BACtrackAPI.Constants.BACtrackUnit

sealed class SdkEvent {
    sealed class ApiKeyEvent: SdkEvent() {
        object Accepted: ApiKeyEvent()
        data class Declined(val message: String): ApiKeyEvent()
    }

    sealed class ConnectedEvent: SdkEvent() {
        data class FoundDevice(val device: Device): ConnectedEvent()
        data class Connected(val deviceType: DeviceType): ConnectedEvent()
        data class DidConnect(val message: String): ConnectedEvent()
        object Disconnected: ConnectedEvent()
        object Timeout: ConnectedEvent()
        object NoDevicesFound: ConnectedEvent()
        object ConnectionError: ConnectedEvent()
    }

    sealed class ReadingEvent: SdkEvent() {
        object Start: ReadingEvent()
        data class Countdown(val count: Int): ReadingEvent()
        object Blow: ReadingEvent()
        object Analyzing: ReadingEvent()
        data class Result(val reading: Float): ReadingEvent()
    }

    sealed class DeviceInformationEvent: SdkEvent() {
        data class Firmware(val firmware: String): DeviceInformationEvent()
        data class Serial(val serial: String): DeviceInformationEvent()
        data class UseCount(val uses: Int): DeviceInformationEvent()
        data class BatteryVoltage(val voltage: Float): DeviceInformationEvent()
        data class BatteryLevel(val level: Int): DeviceInformationEvent()
        data class Units(val units: BACtrackUnit): DeviceInformationEvent()
        data class TransmitPower(val transmitPower: Byte): DeviceInformationEvent()
        data class CalibrationResults(val calibrationResults: ByteArray): DeviceInformationEvent() {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as CalibrationResults

                if (!calibrationResults.contentEquals(other.calibrationResults)) return false

                return true
            }
            override fun hashCode(): Int {
                return calibrationResults.contentHashCode()
            }
        }
        data class ProtectionBit(val protectionBit: ByteArray): DeviceInformationEvent() {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as ProtectionBit

                if (!protectionBit.contentEquals(other.protectionBit)) return false

                return true
            }
            override fun hashCode(): Int {
                return protectionBit.contentHashCode()
            }
        }
    }

    sealed class StateEvent: SdkEvent() {
        object Active: StateEvent()
        object Idle: StateEvent()
    }

    data class ErrorEvent(val error: SdkError): SdkEvent()

    override fun toString(): String {
        return when(this) {
            is ApiKeyEvent -> {
                when(this) {
                    is ApiKeyEvent.Accepted -> "API Key Accepted"
                    is ApiKeyEvent.Declined -> "API Key Declined - $message"
                }
            }
            is ConnectedEvent -> {
                when(this) {
                    is ConnectedEvent.FoundDevice -> "Found Device ${device.readableToString()}"
                    is ConnectedEvent.Connected -> "Connected to $deviceType"
                    is ConnectedEvent.DidConnect -> "DidConnect(${this.message})"
                    is ConnectedEvent.Disconnected -> "Disconnected"
                    is ConnectedEvent.Timeout -> "Timeout"
                    is ConnectedEvent.NoDevicesFound -> "No Devices Found"
                    is ConnectedEvent.ConnectionError -> "Connection Error"
                }
            }
            is ReadingEvent -> {
                when(this) {
                    is ReadingEvent.Start -> "Start"
                    is ReadingEvent.Countdown -> "Countdown... $count"
                    is ReadingEvent.Blow -> "Blow..."
                    is ReadingEvent.Analyzing -> "Analyzing..."
                    is ReadingEvent.Result -> "Result $reading"
                }
            }
            is DeviceInformationEvent -> {
                when(this) {
                    is DeviceInformationEvent.Firmware -> "Firmware $firmware"
                    is DeviceInformationEvent.Serial -> "Serial $serial"
                    is DeviceInformationEvent.UseCount -> "UseCount $uses"
                    is DeviceInformationEvent.BatteryVoltage -> "Battery Voltage $voltage"
                    is DeviceInformationEvent.BatteryLevel -> "Battery Level $level"
                    is DeviceInformationEvent.Units -> "Units $units"
                    is DeviceInformationEvent.CalibrationResults -> "CalibrationResults $calibrationResults"
                    is DeviceInformationEvent.TransmitPower -> "TransmitPower $transmitPower"
                    is DeviceInformationEvent.ProtectionBit -> "ProtectionBit $protectionBit"
                }
            }
            is StateEvent -> {
                when(this) {
                    StateEvent.Active -> "StateEvent.Active"
                    StateEvent.Idle -> "StateEvent.Idle"
                }
            }
            is ErrorEvent -> "$this"
        }
    }
}

class SdkNotInitialized: Exception("SDK was not initialized before use")
class SdkConnectionError: Exception("SDK connection error")

enum class SdkError(val errorCode: Byte) {
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

    override fun toString(): String {
        return when (this) {
            TIME_OUT -> "Connection timeout"
            BLOW_ERROR -> "Blow error"
            OUT_OF_TEMPERATURE -> "Out of Temperature"
            LOW_BATTERY -> "Low Battery"
            CALIBRATION_FAIL -> "Calibration Failure"
            NOT_CALIBRATED -> "Not Calibrated"
            COM_ERROR -> "COM Error"
            INFLOW_ERROR -> "Inflow Error"
            SOLENOID_ERROR -> "Solenoid Error"
            MAX_BAC_EXCEEDED_ERROR -> "Max BAC Exceeded!"
            UNKNOWN_ERROR -> "Unknown Error"
        }
    }

    companion object {
        fun fromByte(errorCode: Byte) = fromInt(errorCode.toInt())

        fun fromInt(errorCode: Int): SdkError {
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

typealias DeviceType = BACTrackDeviceType
typealias Device = BACtrackAPI.BACtrackDevice
typealias DeviceUnit = BACtrackUnit

fun BACtrackAPI.BACtrackDevice.readableToString(): String = "BACtrackDevice(device: ${this.device}, rssi: ${this.rssi}, services: ${this.services}, type: ${this.type})"
