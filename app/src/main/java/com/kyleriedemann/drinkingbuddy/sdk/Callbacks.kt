package com.kyleriedemann.drinkingbuddy.sdk

import BACtrackAPI.API.BACtrackAPI
import BACtrackAPI.API.BACtrackAPICallbacksFull
import BACtrackAPI.Constants.BACTrackDeviceType
import BACtrackAPI.Constants.BACtrackUnit
import androidx.lifecycle.LiveData
import com.github.ajalt.timberkt.Timber.tag
import com.github.ajalt.timberkt.d
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

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

        tag("SdkEvent").d { "$element" }
        Dispatchers.IO.dispatch(GlobalScope.coroutineContext, Runnable {
            this.offer(element)
            allEvents.offer(element)
        })
    }
}

