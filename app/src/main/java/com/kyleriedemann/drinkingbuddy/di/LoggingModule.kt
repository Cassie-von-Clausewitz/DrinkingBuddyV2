package com.kyleriedemann.drinkingbuddy.di

import com.kyleriedemann.drinkingbuddy.sdk.BACtrackDefaultCallbacks
import com.kyleriedemann.drinkingbuddy.sdk.BacTrackFullCallbacks
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import timber.log.Timber

@Module
class LoggingModule {
    @IntoSet
    @Provides
    fun timberDefaultCallbacks(): BACtrackDefaultCallbacks {
        return BACtrackDefaultCallbacks()
    }

    @IntoSet
    @Provides
    fun timberCallbackSet(): BacTrackFullCallbacks {
        return BacTrackFullCallbacks(
            apikeyAcceptedCallbacks = mutableListOf({ Timber.i("API key accepted") }),
            apiKeyDeclinedCallbacks = mutableListOf({ message -> Timber.e("API key declined: $message") }),
            connectedCallbacks = mutableListOf({ type -> Timber.d("Device connected: $type") }),
            didConnectCallbacks = mutableListOf({ message -> Timber.d("Device did connect: $message") }),
            disconnectedCallbacks = mutableListOf({ Timber.d("Device disconnected") }),
            connectionTimeoutCallbacks = mutableListOf({ Timber.v("Connection timed out") }),
            foundBreathalyzerCallbacks = mutableListOf({ device -> Timber.v("Device found: $device") }),
            countdownCallbacks = mutableListOf({ count -> Timber.v("Countdown: $count") }),
            startCallbacks = mutableListOf({ Timber.v("Measurement started") }),
            blowCallbacks = mutableListOf({ Timber.v("Blow!") }),
            analyzingCallbacks = mutableListOf({ Timber.i("Analyzing...") }),
            resultsCallbacks = mutableListOf({ bac -> Timber.i("BAC Result: $bac") }),
            firmwareVersionCallbacks = mutableListOf({ version -> Timber.i("Firmware version: $version") }),
            serialCallbacks = mutableListOf({ serial -> Timber.i("Serial number: $serial") }),
            useCountCallbacks = mutableListOf({ uses -> Timber.i("Use count: $uses") }),
            batteryVoltageCallbacks = mutableListOf({ voltage -> Timber.i("Battery voltage: $voltage") }),
            batteryLevelCallbacks = mutableListOf({ level -> Timber.i("Battery level: $level") }),
            errorCallbacks = mutableListOf({ error -> Timber.e("Error: $error") }),
            unitsCallbacks = mutableListOf({ units -> Timber.d("Units: $units") }),
            onStateActiveCallbacks = mutableListOf({ Timber.v("State: Active") }),
            calibrationResultsCallbacks = mutableListOf({ results -> Timber.i("Calibration results: $results") }),
            connectionErrorCallbacks = mutableListOf({ Timber.w("Connection error") }),
            breathalysersNotFoundCallbacks = mutableListOf({ Timber.w("No devices found") }),
            transmitPowerCallbacks = mutableListOf({ power -> Timber.i("Transmit power: $power") }),
            protectionBitCallbacks = mutableListOf({ protection -> Timber.i("Protection bit: $protection") }),
            onStateIdleCallbacks = mutableListOf({ Timber.v("State: Idle") })
        )
    }
}