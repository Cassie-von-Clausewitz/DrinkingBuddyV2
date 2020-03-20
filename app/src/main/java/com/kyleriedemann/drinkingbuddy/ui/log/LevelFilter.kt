package com.kyleriedemann.drinkingbuddy.ui.log

import com.kyleriedemann.drinkingbuddy.data.models.LogLevel
import com.kyleriedemann.drinkingbuddy.data.models.LogTag
import com.tfcporciuncula.flow.FlowSharedPreferences
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip
import timber.log.Timber
import javax.inject.Inject

data class LevelFilter(
    val verbose: Boolean = false,
    val debug: Boolean = false,
    val info: Boolean = false,
    val warn: Boolean = false,
    val error: Boolean = false,
    val assert: Boolean = false,
    val unknown: Boolean = false
) {
    val values = listOf(verbose, debug, info, warn, error, assert, unknown)
    val isNotEmpty = verbose || debug || info || warn || error || assert || unknown
    val isEmpty = !isNotEmpty

    val intArray = intArrayOf(
        if (verbose) LogLevel.Verbose.level else -1,
        if (debug) LogLevel.Debug.level else -1,
        if (info) LogLevel.Info.level else -1,
        if (error) LogLevel.Warn.level else -1,
        if (assert) LogLevel.Error.level else -1,
        if (unknown) LogLevel.Unknown.level else -1
    ).filter { it > 0 }.toIntArray()

    fun toggle(level: LogLevel): LevelFilter {
        return when(level) {
            LogLevel.Verbose -> copy(verbose = !verbose)
            LogLevel.Debug -> copy(debug = !debug)
            LogLevel.Info -> copy(info = !info)
            LogLevel.Warn -> copy(warn = !warn)
            LogLevel.Error -> copy(error = !error)
            LogLevel.Assert -> copy(assert = !assert)
            LogLevel.Unknown -> copy(unknown = !unknown)
        }
    }

    fun stringSet(): Set<String?> {
        val set = mutableSetOf<String?>()
        if (verbose) set.add(LogLevel.Verbose.name)
        if (debug) set.add(LogLevel.Debug.name)
        if (info) set.add(LogLevel.Info.name)
        if (warn) set.add(LogLevel.Warn.name)
        if (error) set.add(LogLevel.Error.name)
        if (assert) set.add(LogLevel.Assert.name)
        if (unknown) set.add(LogLevel.Unknown.name)
        return set
    }

    companion object {
        fun fromStringSet(stringSet: Set<String?>): LevelFilter {
            Timber.d("LevelFilter.fromStringSet($stringSet)")
            return LevelFilter(
                verbose = stringSet.contains(LogLevel.Verbose.name),
                debug = stringSet.contains(LogLevel.Debug.name),
                info = stringSet.contains(LogLevel.Info.name),
                warn = stringSet.contains(LogLevel.Warn.name),
                error = stringSet.contains(LogLevel.Error.name),
                assert = stringSet.contains(LogLevel.Assert.name),
                unknown = stringSet.contains(LogLevel.Unknown.name)
            )
        }
    }
}

data class TagFilter(val logTags: List<LogTag>) {
    val isNotEmpty = logTags.isNotEmpty()
    val isEmpty = !isNotEmpty

    val array = stringSet().toTypedArray()

    fun contains(tag: String): Boolean {
        return logTags.contains(LogTag(tag = tag, selected = false))
    }

    fun toggle(logTag: LogTag): TagFilter {
        Timber.v("TagFilter.toggle($logTag): $this")
        return if (logTags.contains(logTag)) {
            val list = logTags.toMutableList()
            list.remove(logTag)

            copy(logTags = list)
        } else {
            val list = logTags.toMutableList()
            list.add(logTag)

            copy(logTags = list)
        }
    }

    fun stringSet(): Set<String> {
        return logTags.map { it.tag ?: "" }.filter { it.isNotEmpty() }.toSet()
    }

    companion object {
        fun fromStringSet(stringSet: Set<String>): TagFilter {
            Timber.d("TagFilter.fromStringSet($stringSet)")
            return TagFilter(stringSet.map { LogTag(it) })
        }
    }
}

data class LogFilters(val levelFilter: LevelFilter, val tagFilter: TagFilter) {
    val isNotEmpty = levelFilter.isNotEmpty || tagFilter.isNotEmpty
    val isEmpty = !isNotEmpty
}

const val logLevelFilterKey = "logLevelFilterKey"
const val logTagFilterKey = "logTagFilterKey"
class FilterRepository @Inject constructor(private val preferences: FlowSharedPreferences) {

    private val levelPreference = preferences.getNullableStringSet(logLevelFilterKey)
    private val tagPreference = preferences.getStringSet(logTagFilterKey)

    val levelFilter = levelPreference.asFlow().map { LevelFilter.fromStringSet(it) }
    val tagFilter = tagPreference.asFlow().map { TagFilter.fromStringSet(it) }

    private val zip = levelFilter.zip(tagFilter) { level, tag -> LogFilters(level, tag) }
    private val combine = combine(levelFilter, tagFilter) { level, tag -> LogFilters(level, tag) }

    val filter = combine

    suspend fun toggleLogLevel(logLevel: LogLevel) =
        levelPreference.setAndCommit(LevelFilter.fromStringSet(levelPreference.get()).toggle(logLevel).stringSet())

    suspend fun toggleLogTag(logTag: LogTag) =
        tagPreference.setAndCommit(TagFilter.fromStringSet(tagPreference.get()).toggle(logTag).stringSet())

    suspend fun clearFilter() {
        levelPreference.deleteAndCommit()
        tagPreference.deleteAndCommit()
    }
}