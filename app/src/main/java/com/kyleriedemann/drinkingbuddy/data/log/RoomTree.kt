package com.kyleriedemann.drinkingbuddy.data.log

import android.annotation.SuppressLint
import android.util.Log as AndroidLog
import com.kyleriedemann.drinkingbuddy.data.models.Log
import com.kyleriedemann.drinkingbuddy.data.models.LogLevel
import com.kyleriedemann.drinkingbuddy.data.source.local.LogDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

class RoomTree(private val logDao: LogDao, private val dispatcher: CoroutineDispatcher) : Timber.DebugTree() {
    @SuppressLint("LogNotTimber")
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (tag == null) {
            AndroidLog.d("RoomTree", "Missed saving log with message [$message]")
            return
        }
        CoroutineScope(dispatcher).launch {
            logDao.insertLog(Log(level = LogLevel.fromLevel(priority), tag = tag, message = message, error = t?.message))
        }
    }
}